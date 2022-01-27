FROM quay.io/kiegroup/kogito-runtime-jvm:latest

COPY target/quarkus-app/quarkus-run.jar $KOGITO_HOME/bin

COPY target/quarkus-app/lib $KOGITO_HOME/bin/lib
COPY target/quarkus-app/quarkus $KOGITO_HOME/bin/quarkus
COPY target/quarkus-app/app $KOGITO_HOME/bin/app