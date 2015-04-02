FROM maven:3-jdk-8

WORKDIR /build
ADD src /build/src
RUN ls;pwd;mvn clean compile
CMD ls;pwd;mvn exec:java