FROM maven:3-jdk-8

WORKDIR /build
ADD . /build

RUN mvn -e clean compile

CMD mvn -e exec:java