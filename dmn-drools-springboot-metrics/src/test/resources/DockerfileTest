FROM fabric8/java-alpine-openjdk11-jre

COPY target/dmn-drools-springboot-metrics.jar /deployments/

ENTRYPOINT [ "/deployments/run-java.sh" ]