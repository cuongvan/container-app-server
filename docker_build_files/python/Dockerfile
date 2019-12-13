FROM python:3-alpine

# Add non root user
RUN addgroup -S app && adduser app -S -G app
RUN chown -R app /home/app && mkdir -p /home/app/python && chown -R app /home/app
ENV PATH=$PATH:/home/app/.local/bin:/home/app/python/bin/
ENV PYTHONPATH=$PYTHONPATH:/home/app/python
RUN touch /home/app/requirements.txt
USER app
WORKDIR /home/app/
CMD ["python", "main.py"]

COPY code/ /home/app/
RUN pip install -r requirements.txt --target=/home/app/python