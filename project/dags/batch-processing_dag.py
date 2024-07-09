from airflow import DAG
from airflow.operators.python_operator import PythonOperator
from airflow.providers.mysql.hooks.mysql import MySqlHook
from airflow.providers.mysql.operators.mysql import MySqlOperator
from datetime import datetime
from dateutil.relativedelta import relativedelta
import logging
from sqlalchemy import create_engine, text

default_args = {
    'owner': 'airflow',
    'start_date': datetime(2024, 6, 1),
    'retries': 0,
}

dag = DAG(
    'etl_to_analytics_database_v3',
    default_args=default_args,
    description='ETL process from app DB to analytics DB',
    schedule_interval='@monthly',
)

app_db_conn_id = 'mysql_localhost'
analytics_db_conn_id = 'analytics_localhost'


def extract_data_from_staging(**kwargs):
    mysql_hook = MySqlHook(mysql_conn_id=app_db_conn_id)
    roles = mysql_hook.get_records("SELECT * FROM roles")
    users = mysql_hook.get_records("SELECT * FROM users")

    today = datetime.today()
    one_month_ago = today - relativedelta(months=1)
    #custom_delta = today - relativedelta(days=1)
    one_month_ago_str = one_month_ago.strftime('%Y-%m-%d')

    expense_query = f"SELECT * FROM expense WHERE date >= '{one_month_ago_str}'"
    income_query = f"SELECT * FROM income WHERE date >= '{one_month_ago_str}'"

    expenses = mysql_hook.get_records(expense_query)
    incomes = mysql_hook.get_records(income_query)
    expense_groups = mysql_hook.get_records("SELECT * FROM expense_group")
    income_groups = mysql_hook.get_records("SELECT * FROM income_group")

    return users, expenses, incomes, expense_groups, income_groups, roles


def transform_data(**kwargs):
    users, expenses, incomes, expense_groups, income_groups, roles = kwargs['ti'].xcom_pull(task_ids='extract_data_from_staging')

    role_map = {r[0]: r[1] for r in roles}
    user_map = {u[0]: u[4] for u in users}
    expense_group_map = {eg[0]: eg[2] for eg in expense_groups}
    income_group_map = {ig[0]: ig[2] for ig in income_groups}

    dim_users = [{'id': u[0], 'username': u[4], 'role': role_map[u[5]], 'age': u[6], 'gender': u[7], 'country': u[8]} for u in users]
    dim_expense_groups = [{'id': eg[0], 'transaction_type': 'expense', 'description': eg[1], 'name': eg[2]} for eg in expense_groups]
    dim_income_groups = [{'id': ig[0], 'transaction_type': 'income', 'description': ig[1], 'name': ig[2]} for ig in income_groups]

    fact_expenses = [{'id': e[0], 'username': user_map[e[5]], 'expense_group': expense_group_map[e[4]], 'amount': e[1], 'date': e[2], 'description': e[3]} for e in expenses]
    fact_incomes = [{'id': i[0], 'username': user_map[i[5]], 'income_group': income_group_map[i[4]], 'amount': i[1], 'date': i[2], 'description': i[3]} for i in incomes]

    return dim_users, dim_expense_groups, dim_income_groups, fact_expenses, fact_incomes, user_map


def load_data(**kwargs):
    dim_users, dim_expense_groups, dim_income_groups, fact_expenses, fact_incomes, user_map = kwargs['ti'].xcom_pull(task_ids='transform_data')

    mysql_hook = MySqlHook(mysql_conn_id=analytics_db_conn_id)
    connection = mysql_hook.get_conn()
    cursor = connection.cursor()

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS dim_user_analytics(
            id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
            username VARCHAR(50) NOT NULL UNIQUE,
            age INT,
            gender VARCHAR(50),
            country VARCHAR(255),
            role VARCHAR(255) NOT NULL
        )
    """)
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS dim_transaction_group_analytics(
            id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
            transaction_type VARCHAR(50) NOT NULL,
            description VARCHAR(255) NOT NULL,
            name VARCHAR(255) UNIQUE NOT NULL
        )
    """)
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS fact_transaction_analytics(
            id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
            date DATETIME NOT NULL,
            description VARCHAR(255) NOT NULL,
            amount INT NOT NULL,
            user_id INT NOT NULL,
            transaction_group_id INT,
            FOREIGN KEY (user_id) REFERENCES dim_user_analytics(id),
            FOREIGN KEY (transaction_group_id) REFERENCES dim_transaction_group_analytics(id)
        )
    """)

    for user in dim_users:
        if user:

            cursor.execute("""
                INSERT INTO dim_user_analytics (username, role, age, gender, country)
                VALUES (%s, %s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE
                username = VALUES(username)
            """, (user['username'], user['role'], user['age'], user['gender'], user['country']))
        else:
            raise ValueError(f"User not found in dim_user_analytics")

    for group in dim_expense_groups:
        cursor.execute("""
            INSERT INTO dim_transaction_group_analytics (transaction_type, description, name)
            VALUES (%s, %s, %s)
            ON DUPLICATE KEY UPDATE
            name = VALUES(name)
        """, (group['transaction_type'], group['description'], group['name']))

    for group in dim_income_groups:
        cursor.execute("""
            INSERT INTO dim_transaction_group_analytics (transaction_type, description, name)
            VALUES (%s, %s, %s)
            ON DUPLICATE KEY UPDATE
            name = VALUES(name)
        """, (group['transaction_type'], group['description'], group['name']))

    for expense in fact_expenses:
        cursor.execute("SELECT id FROM dim_user_analytics WHERE username = %s", (expense['username'],))
        expense['user_id'] = cursor.fetchone()[0]
        cursor.execute("SELECT id FROM dim_transaction_group_analytics WHERE name = %s", (expense['expense_group'],))
        expense['transaction_group_id'] = cursor.fetchone()[0]
        cursor.execute("""
            INSERT INTO fact_transaction_analytics (date, description, amount, user_id, transaction_group_id)
            VALUES (%s, %s, %s, %s, %s)
        """, (expense['date'], expense['description'], expense['amount'], expense['user_id'], expense['transaction_group_id']))

    for income in fact_incomes:
        cursor.execute("SELECT id FROM dim_user_analytics WHERE username = %s", (income['username'],))
        income['user_id'] = cursor.fetchone()[0]
        cursor.execute("SELECT id FROM dim_transaction_group_analytics WHERE name = %s", (income['income_group'],))
        income['transaction_group_id'] = cursor.fetchone()[0]
        cursor.execute("""
            INSERT INTO fact_transaction_analytics (date, description, amount, user_id, transaction_group_id)
            VALUES (%s, %s, %s, %s, %s)
        """, (income['date'], income['description'], income['amount'], income['user_id'], income['transaction_group_id']))

    connection.commit()
    cursor.close()
    connection.close()

extract_task = PythonOperator(
    task_id='extract_data_from_staging',
    python_callable=extract_data_from_staging,
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

