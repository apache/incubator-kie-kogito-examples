FROM --platform=linux/amd64 registry.access.redhat.com/ubi9/openjdk-17:1.18

ENV RUNTIME_TYPE quarkus

COPY target/*-runner.jar $KOGITO_HOME/bin
COPY target/lib $KOGITO_HOME/bin/lib