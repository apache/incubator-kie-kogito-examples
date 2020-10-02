FROM adoptopenjdk:11-jre-hotspot

RUN mkdir -p /opt/app/lib
COPY target/notification-service-1.0.0-SNAPSHOT-runner.jar /opt/app
COPY target/lib/*.jar /opt/app/lib

CMD ["java", "-jar", "/opt/app/notification-service-1.0.0-SNAPSHOT-runner.jar"]