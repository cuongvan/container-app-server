FROM openjdk:8-jre-slim


# create user to run app
RUN useradd --create-home ckanapp


# initialize output dirs & files
RUN mkdir /input-files && chown ckanapp:ckanapp /input-files && \
    mkdir /outputs /outputs/files && \
    echo '{"params": []}' > /outputs/output.json && \
    chown -R ckanapp:ckanapp /outputs && \
    mkdir /app && chown -R ckanapp:ckanapp /app


WORKDIR /app


# copy app code
COPY . .


# container execution config
USER ckanapp

CMD ["java", "-jar", "Main.jar"]
