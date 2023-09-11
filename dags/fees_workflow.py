from datetime import timedelta, datetime
from airflow import DAG
from airflow.operators.http_operator import SimpleHttpOperator
from airflow.operators.python_operator import PythonOperator

def extract_fee_id(response):
    return response.json()['transaction_id']

def calculate_and_extract_fee_id(**context):
    response = context['ti'].xcom_pull(task_ids='calculate_fee')
    fee_id = extract_fee_id(response)
    context['ti'].xcom_push(key='fee_id', value=fee_id)

def calculate_fee_task(dag):
    return SimpleHttpOperator(
        task_id='calculate_fee',
        method='POST',
        endpoint='/transaction/fee',
        http_conn_id='fee-app',
        data={
            "transaction_id": "txn_001",
            "amount": 1000,
            "asset": "USD",
            "asset_type": "FIAT",
            "type": "MobileTopUp",
            "state": "SETTLED",
            "created_at": "2023-08-30T15:42:17.610059"
        },
        headers={"Content-Type": "application/json"},
        xcom_push=True,
        dag=dag,
    )

def calculate_and_extract_fee_id_task(dag):
    return PythonOperator(
        task_id='calculate_and_extract_fee_id',
        python_callable=calculate_and_extract_fee_id,
        provide_context=True,
        dag=dag,
    )

def charge_fee_task(dag):
    return SimpleHttpOperator(
        task_id='charge_fee',
        method='POST',
        endpoint='/transaction/fee/{{ task_instance.xcom_pull(task_ids=\'calculate_and_extract_fee_id\', key=\'fee_id\') }}/charge',
        http_conn_id='fee-app',
        headers={"Content-Type": "application/json"},
        retries=3,
        retry_delay=timedelta(minutes=1),
        dag=dag,
    )

def record_fee_task(dag):
    return SimpleHttpOperator(
        task_id='record_fee',
        method='PATCH',
        endpoint='/transaction/fee/{{ task_instance.xcom_pull(task_ids=\'calculate_and_extract_fee_id\', key=\'fee_id\') }}/status',
        http_conn_id='fee-app',
        data={
            "status": "COMPLETED"
        },
        headers={"Content-Type": "application/json"},
        dag=dag,
    )

default_args = {
    'owner': 'airflow',
    'start_date': datetime(2023, 8, 30),
    'retry_delay': timedelta(minutes=5),
}

dag = DAG(
    'fee_calculation_workflow',
    default_args=default_args,
    description='Workflow for transaction fee calculation',
    schedule_interval=timedelta(days=1),
)

calculate_fee = calculate_fee_task(dag)
calculate_and_extract_fee_id = calculate_and_extract_fee_id_task(dag)
charge_fee = charge_fee_task(dag)
record_fee = record_fee_task(dag)

calculate_fee >> calculate_and_extract_fee_id >> charge_fee >> record_fee
