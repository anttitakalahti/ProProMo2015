FROM maven:3-jdk-8

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

ONBUILD ADD . /usr/src/app


RUN cd /usr/src/app
RUN ls
RUN mvn clean compile exec:java