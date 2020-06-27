FROM python:3.8-slim


# create user to run app
RUN useradd --create-home ckanapp


# install defaults requirements
RUN pip install --upgrade pip && \
    pip install requests

# matplotlib numpy


# initialize output dirs & files
RUN mkdir /outputs /outputs/files && \
    echo '{"params": []}' > /outputs/output.json && \
    chown -R ckanapp:ckanapp /outputs


# work dir
RUN mkdir /app
WORKDIR /app


# install app's requirements, add main.py to avoid no source files
COPY main.py ./requirements.txt* ./
RUN if [ -f requirements.txt ]; then pip install -r requirements.txt; fi


# copy app code
COPY ./ ./
RUN chown -R ckanapp:ckanapp /app


# container execution config
USER ckanapp

CMD ["python", "main.py"]
