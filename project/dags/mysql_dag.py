from airflow import DAG
from airflow.operators.python_operator import PythonOperator
from airflow.providers.mysql.hooks.mysql import MySqlHook
from airflow.providers.mysql.operators.mysql import MySqlOperator
from datetime import datetime, timedelta
from sqlalchemy import create_engine, text

default_args = {
    'owner': 'airflow',
    'retries': 2,
    "retry_delay": timedelta(seconds=10)
}



with DAG(
    dag_id='dag_with_mysql_operator_v04',
    default_args=default_args,
    start_date=datetime(2024, 6, 18),
    schedule_interval='0 0 * * *'
) as dag:
    task1 = MySqlOperator(
        task_id='create_table',
        mysql_conn_id='mysql_localhost',
        sql="""
            create table if not exists dag_runs (
                dt date,
                dag_id varchar(50),
                primary key (dt, dag_id)
            )
        """
    )

    task2 = MySqlOperator(
        task_id='insert_into_table',
        mysql_conn_id='mysql_localhost',
        sql="""
            insert into dag_runs (dt, dag_id) values ('{{ ds }}', '{{ dag.dag_id }}')
        """
    )

    task3 = MySqlOperator(
        task_id='delete_data_from_table',
        mysql_conn_id='mysql_localhost',
        sql="""
            delete from dag_runs where dt = '{{ ds }}' and dag_id = '{{ dag.dag_id }}';
        """
    )
    task1 >> task3 >> task2
    