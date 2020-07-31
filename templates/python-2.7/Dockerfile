FROM python:2.7.18


# install defaults requirements
RUN pip install --upgrade pip && \
    pip install requests matplotlib pandas ckanapi

RUN groupadd -g 20000 ckanapp && useradd --create-home -u 20000 -g ckanapp ckanapp

# initialize output dirs & files
RUN mkdir /inputs /outputs /app &&\
    chown -R ckanapp:ckanapp /inputs /outputs /app
    
USER ckanapp

WORKDIR /app

# install libraries
COPY --chown=ckanapp:ckanapp requirements.txt .

RUN pip install --user -r requirements.txt

# app code
COPY --chown=ckanapp:ckanapp . .

ENV TIMEOUT 60

CMD ["sh", "-c", "/usr/bin/timeout --kill-after=5 $TIMEOUT python main.py"]
