FROM quay.io/kiegroup/kogito-runtime-jvm:latest

ENV RUNTIME_TYPE quarkus

COPY target/*-runner.jar $KOGITO_HOME/bin
COPY target/lib $KOGITO_HOME/bin/lib