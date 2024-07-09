from airflow import DAG
from airflow.operators.python_operator import PythonOperator
from datetime import datetime
from sqlalchemy import create_engine, text

default_args = {
    'owner': 'airflow',
    'start_date': datetime(2024, 6, 18),
    'retries': 0,
}

dag = DAG(
    'etl_to_analytics_db',
    default_args=default_args,
    description='ETL process from app DB to analytics DB',
    schedule_interval='@daily',
)

app_db_conn_str = 'mysql://root:root@localhost:3307/testdb'
analytics_db_conn_str = 'mysql://{0}:{1}@{2}:{3}'.format("root", "root", "localhost", 3308)


def extract_data():

    engine = create_engine(app_db_conn_str)
    with engine.connect() as conn:
        roles = conn.execute("SELECT * FROM roles").fetchall()
        users = conn.execute("SELECT * FROM users").fetchall()
        expenses = conn.execute("SELECT * FROM expense").fetchall()
        incomes = conn.execute("SELECT * FROM income").fetchall()
        expense_groups = conn.execute("SELECT * FROM expense_groups").fetchall()
        income_groups = conn.execute("SELECT * FROM income_groups").fetchall()

    return users, expenses, incomes, expense_groups, income_groups, roles


def transform_data(**kwargs):
    users, expenses, incomes, expense_groups, income_groups, roles = kwargs['ti'].xcom_pull(task_ids='extract_data')

    role_map = {r[0]: r[1] for r in roles}
    user_map = {u[0]: u[1] for u in users}
    expense_group_map = {eg[0]: eg[1] for eg in expense_groups}
    income_group_map = {ig[0]: ig[1] for ig in income_groups}

    dim_users = [{'id': u[0], 'username': u[1], 'role': role_map[u[3]]} for u in users]
    dim_expense_groups = [{'id': eg[0], 'transaction_type': 'expense', 'description': eg[2], 'name': eg[1]} for eg in expense_groups]
    dim_income_groups = [{'id': ig[0], 'transaction_type': 'income', 'description': ig[2], 'name': ig[1]} for ig in income_groups]

    fact_expenses = [{'id': e[0], 'username': user_map[e[5]], 'expense_group': expense_group_map[e[4]], 'amount': e[2], 'date': e[3], 'description': e[1]} for e in expenses]
    fact_incomes = [{'id': i[0], 'username': user_map[i[5]], 'income_group': income_group_map[i[4]], 'amount': i[2], 'date': i[3], 'description': i[1]} for i in incomes]

    return dim_users, dim_expense_groups, dim_income_groups, fact_expenses, fact_incomes, user_map


def load_data(**kwargs):
    dim_users, dim_expense_groups, dim_income_groups, fact_expenses, fact_incomes, user_map = kwargs['ti'].xcom_pull(task_ids='transform_data')

    engine = create_engine(analytics_db_conn_str)
    with engine.connect() as conn:

        conn.execute(text("""create table if not exists dim_user(
                id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
                username varchar(50) NOT NULL unique,
                role varchar(255) NOT NULL
            )"""))
        conn.execute(text("""create table if not exists dim_transaction_group(
            id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
            transaction_type varchar(50) NOT NULL,
            description varchar(50) NOT NULL,
            name varchar(255) unique NOT NULL
            )"""))
        conn.execute(text("""CREATE TABLE if not exists fact_transaction(
                id int PRIMARY KEY NOT NULL auto_increment,
                date datetime NOT NULL,
                description varchar(50) NOT NULL,
                amount int NOT NULL,
                user_id int NOT NULL,
                transaction_group_id int,
                FOREIGN KEY (user_id) REFERENCES dim_user(id),
                FOREIGN KEY (transaction_group_id) REFERENCES dim_transaction_group(id)
            )"""))

        for user in dim_users:
            #conn.execute("INSERT INTO dim_user (username, role) VALUES (:user_id, :username, :role_id, :role_name)", **user)
            conn.execute(
                text("""
                INSERT INTO dim_user (username, role)
                VALUES (:username, :role)
                ON DUPLICATE KEY UPDATE
                username = VALUES(username)
                """), **user
            )

        for group in dim_expense_groups:
            conn.execute(
                text("""
                INSERT INTO dim_transaction_group (transaction_type, description, name)
                VALUES (:transaction_type, :description, :name)
                ON DUPLICATE KEY UPDATE
                name = VALUES(name)
                """), **group
            )

        for group in dim_income_groups:
            conn.execute(
                text("""
                INSERT INTO dim_transaction_group (transaction_type, description, name)
                VALUES (:transaction_type, :description, :name)
                ON DUPLICATE KEY UPDATE
                name = VALUES(name)
                """), **group
            )

        for expense in fact_expenses:
            expense['user_id'] = conn.execute(text("SELECT id FROM dim_users WHERE username = :username"), {'username': expense['username']}).fetchone()[0]
            expense['transaction_group_id'] = conn.execute(text("SELECT id FROM dim_expense_groups WHERE name = :name"), {'name': expense['expense_group']}).fetchone()[0]
            conn.execute(
                text("""
                INSERT INTO fact_transaction (date, description, amount, user_id, transaction_group_id)
                VALUES (:date, :description, :amount, :description, :user_id, :transaction_group_id)
                """), **expense
            )

        for income in fact_incomes:
            income['user_id'] = conn.execute(text("SELECT id FROM dim_users WHERE username = :username"), {'username': income['username']}).fetchone()[0]
            income['transaction_group_id'] = conn.execute(text("SELECT id FROM dim_expense_groups WHERE name = :name"), {'name': income['income_group']}).fetchone()[0]
            conn.execute(
                text("""
                INSERT INTO fact_transaction (date, description, amount, user_id, transaction_group_id)
                VALUES (:date, :description, :amount, :description, :user_id, :transaction_group_id)
                """), **income
            )

extract_task = PythonOperator(
    task_id='extract_data',
    python_callable=extract_data,
    dag=dag,
)

transform_task = PythonOperator(
    task_id='transform_data',
    python_callable=transform_data,
    provide_context=True,
    dag=dag,
)

load_task = PythonOperator(
    task_id='load_data',
    python_callable=load_data,
    provide_context=True,
    dag=dag,
)

extract_task >> transform_task >> load_task

