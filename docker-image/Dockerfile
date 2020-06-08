FROM openjdk:8u252-jre-slim

RUN useradd --create-home app
USER app

# appserver will see this value
# ENV DATA_DIR=/data

ADD appserver.tar /home/app/

WORKDIR /home/app/appserver

ENTRYPOINT ["bin/appserver"]