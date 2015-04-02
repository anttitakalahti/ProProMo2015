FROM maven:3-jdk-8

CMD mvn clean compile exec:java
