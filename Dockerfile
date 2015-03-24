FROM java:8
WORKDIR /build
ADD src /build/src
RUN javac src/org/duvin/propromo2015
RUN ["java" "org.duvin.propromo2015.Example"]
