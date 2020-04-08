FROM python:3.8-buster

WORKDIR /app

RUN touch /app/requirements.txt
RUN pip install --upgrade pip

COPY code/requirements.txt* /app/requirements.txt
RUN pip install -r requirements.txt

COPY code/ /app/
CMD ["python", "main.py"]
