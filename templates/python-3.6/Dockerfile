FROM python:3.8-slim


# create user to run app
RUN useradd --create-home ckanapp


# install defaults requirements
RUN pip install --upgrade pip && \
    pip install requests

# matplotlib numpy


# initialize output dirs & files
RUN mkdir /inputs /outputs /app &&\
    chown -R ckanapp:ckanapp /inputs /outputs /app
    
# work dir
WORKDIR /app

# install app's requirements, add main.py to avoid no source files
COPY --chown=ckanapp:ckanapp requirements.txt .
RUN pip install -r requirements.txt


# copy app code
COPY --chown=ckanapp:ckanapp . .

USER ckanapp

CMD ["python", "main.py"]
