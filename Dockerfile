FROM maven:3-jdk-8

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

ADD . /usr/src/app


RUN cd /usr/src/app; pwd; ls; mvn clean compile exec:java