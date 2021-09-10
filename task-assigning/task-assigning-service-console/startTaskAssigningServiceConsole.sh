#!/bin/bash

COMMAND="\
-Dquarkus.http.port=8480 \
-Dkogito.task-assigning.service.solution.url=http://localhost:8380/task-assigning/service/solution \
-jar target/task-assigning-service-console-2.0.0-SNAPSHOT-runner.jar"

eval java $COMMAND
