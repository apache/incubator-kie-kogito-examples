#FROM maven:3.6.2-jdk-8 
FROM openjdk:8
COPY . /
WORKDIR target
CMD ["java", "-jar", "drools-quarkus-metrics-8.0.0-SNAPSHOT-runner.jar"]
#CMD ["./mvnw", "compile", "quarkus:dev"]
#CMD ["sleep", "10000"] 

