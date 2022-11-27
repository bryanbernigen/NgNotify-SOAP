FROM maven:3-openjdk-11

# Create app directory
WORKDIR /usr/src/app

COPY webservice/ ./

RUN mvn clean compile assembly:single

COPY . .

CMD ["java", "-jar" ,"target/webservice-1.0-SNAPSHOT-jar-with-dependencies.jar"]