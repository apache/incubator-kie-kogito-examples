FROM quay.io/kiegroup/kogito-runtime-jvm:2.44

ENV RUNTIME_TYPE quarkus

COPY target/*-runner.jar $KOGITO_HOME/bin
COPY target/lib $KOGITO_HOME/bin/lib