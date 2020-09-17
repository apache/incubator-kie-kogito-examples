#!/bin/sh
echo "Starting the Kogito Management Console"

PROJECT_VERSION=$(cd ../ && mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

echo "Project version: ${PROJECT_VERSION}"

MANAGEMENT_CONSOLE_VERSION=${PROJECT_VERSION}
MANAGEMENT_CONSOLE_RUNNER="https://repository.jboss.org/nexus/service/local/artifact/maven/content?r=public&g=org.kie.kogito&a=management-console&v=${MANAGEMENT_CONSOLE_VERSION}&c=runner"


wget -nc -O management-console-${MANAGEMENT_CONSOLE_VERSION}-runner.jar ${MANAGEMENT_CONSOLE_RUNNER}

java -jar  -Dquarkus.http.port=8280 management-console-${MANAGEMENT_CONSOLE_VERSION}-runner.jar
