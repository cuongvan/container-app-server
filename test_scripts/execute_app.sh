APP_ID=$1
http -f :5001/app/$APP_ID/execute?userId=cuongvan algorithm=k-anomity file@../README.md
