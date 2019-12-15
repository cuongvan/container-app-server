FROM python:3-alpine

WORKDIR /app

RUN touch /app/requirements.txt
COPY code/requirements.txt* /app/requirements.txt
RUN pip install -r requirements.txt

COPY code/ /app/
CMD ["python", "main.py"]
