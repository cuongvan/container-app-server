FROM gradle:6.0-jdk8
COPY extract.sh /
RUN chmod +x /extract.sh
WORKDIR /app
COPY code/build.gradle .
RUN gradle --no-daemon dependencies

COPY code/ /app/
RUN gradle --no-daemon distTar
WORKDIR /app/build/distributions
RUN /extract.sh

# mv project dist directory to to /dist
# and change run script to "run.sh"
RUN rm /extract.sh
CMD ["/dist/bin/run.sh"]