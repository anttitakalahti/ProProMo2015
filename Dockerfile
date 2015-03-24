FROM java:8
WORKDIR /build
ADD src /build/src
RUN javac src/org/duvin/propromo2015/Example.java
CMD cd src && java org.duvin.propromo2015.Example
