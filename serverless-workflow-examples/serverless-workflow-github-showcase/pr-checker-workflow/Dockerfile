FROM quay.io/kiegroup/kogito-runtime-jvm:1.43

ENV RUNTIME_TYPE quarkus

COPY target/*-runner.jar $KOGITO_HOME/bin
COPY target/lib $KOGITO_HOME/bin/lib