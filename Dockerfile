FROM maven:3-openjdk-11
COPY /webservice/ ./
RUN mvn clean compile assembly:single
CMD ["java", "-jar" ,"target/webservice-1.0-SNAPSHOT-jar-with-dependencies.jar"]