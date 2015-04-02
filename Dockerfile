FROM maven:3-jdk-8

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

ADD . /usr/src/app

ONBUILD RUN mvn clean compile


RUN cd /usr/src/app; mvn exec:java