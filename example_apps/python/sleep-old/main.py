import os, time

if __name__ == "__main__":
    params = os.getenv('INPUT', '0')
    sleep_duration = int(params)
    print('Sleeping for', sleep_duration, 'seconds...', flush=True)
    time.sleep(sleep_duration)
    print('Woke up!')
    