#!/bin/sh
echo "Starting the Kogito Management Console"

MANAGEMENT_CONSOLE_VERSION=0.9.0
MANAGEMENT_CONSOLE_RUNNER=https://search.maven.org/remotecontent?filepath=org/kie/kogito/management-console/${MANAGEMENT_CONSOLE_VERSION}/management-console-${MANAGEMENT_CONSOLE_VERSION}-runner.jar

wget -nc -O management-console-${MANAGEMENT_CONSOLE_VERSION}-runner.jar ${MANAGEMENT_CONSOLE_RUNNER}

java -jar  -Dquarkus.http.port=8280 management-console-${MANAGEMENT_CONSOLE_VERSION}-runner.jar
