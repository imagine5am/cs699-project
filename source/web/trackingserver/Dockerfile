FROM openjdk:8
# VOLUME /tmp

MAINTAINER Shivam Sood <shivamsood.code@gmail.com>

ADD build/libs/*.war app.war
EXPOSE 8080

RUN apt-get update && apt-get install -y wget

ENV DOCKERIZE_VERSION v0.6.1
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

CMD dockerize -wait tcp://mysql:3306 -timeout 60m \
    java -Djava.security.egd=file:/dev/./urandom -jar /app.war
