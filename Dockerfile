FROM java:8
RUN mvn clean compile exec:java
