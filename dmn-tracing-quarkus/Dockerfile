FROM quay.io/kiegroup/kogito-runtime-jvm:latest

ENV RUNTIME_TYPE quarkus

COPY target/quarkus-app/lib/ $KOGITO_HOME/bin/lib/
COPY target/quarkus-app/*.jar $KOGITO_HOME/bin
COPY target/quarkus-app/app/ $KOGITO_HOME/bin/app/
COPY target/quarkus-app/quarkus/ $KOGITO_HOME/bin/quarkus/

# For the legacy quarkus application jar use the commands below
# COPY target/*-runner.jar $KOGITO_HOME/bin
# COPY target/lib $KOGITO_HOME/bin/lib
