FROM maven:3-jdk-8

WORKDIR /build
ADD src /build/src
RUN mvn clean compile
CMD pwd;mvn exec:java