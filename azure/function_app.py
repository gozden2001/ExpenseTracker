import logging
import requests
import azure.functions as func

app = func.FunctionApp()

@app.schedule(schedule="0 * * * * *", arg_name="myTimer", run_on_startup=True,
              use_monitor=False) 
def emailReports(myTimer: func.TimerRequest) -> None:
    if myTimer.past_due:
        logging.info('The timer is past due!')

    spring_app_url = "http://localhost:8081/test/send-email/scheduled-emails-test"
    
    try:
        response = requests.post(spring_app_url)
        
        if response.status_code == 200:
            logging.info("Scheduled task triggered successfully.")
        else:
            logging.error("Failed to trigger scheduled task.")
    except Exception as e:
        logging.error(f"Error triggering scheduled task: {e}")




@app.timer_trigger(schedule="0 0 8 * * MON", arg_name="myTimer", run_on_startup=False,
              use_monitor=False) 
def weeklyReport(myTimer: func.TimerRequest) -> None:
    
    if myTimer.past_due:
        logging.info('The timer is past due!')

    spring_app_url = "http://localhost:8081/test/send-email/scheduled-emails/weekly"
    
    try:
        response = requests.post(spring_app_url)
        
        if response.status_code == 200:
            logging.info("Scheduled task triggered successfully.")
        else:
            logging.error("Failed to trigger scheduled task.")
    except Exception as e:
        logging.error(f"Error triggering scheduled task: {e}")




@app.timer_trigger(schedule="0 0 8 1 * *", arg_name="myTimer", run_on_startup=False,
              use_monitor=False) 
def monthlyReport(myTimer: func.TimerRequest) -> None:
    
    if myTimer.past_due:
        logging.info('The timer is past due!')

    spring_app_url = "http://localhost:8081/test/send-email/scheduled-emails/monthly"
    
    try:
        response = requests.post(spring_app_url)
        
        if response.status_code == 200:
            logging.info("Scheduled task triggered successfully.")
        else:
            logging.error("Failed to trigger scheduled task.")
    except Exception as e:
        logging.error(f"Error triggering scheduled task: {e}")