FROM openjdk:8-jre-slim

RUN useradd --create-home ckanapp
USER ckanapp
WORKDIR /home/ckanapp/app

# copy contents of code/ into app/
ADD code .

CMD ["java", "-jar", "Main.jar"]