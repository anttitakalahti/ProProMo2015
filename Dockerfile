FROM maven:3-jdk-8

WORKDIR /build
ADD . /build

RUN mvn clean compile

CMD mvn exec:java