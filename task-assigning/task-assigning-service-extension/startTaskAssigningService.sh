#!/bin/bash

COMMAND="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:5009 \
-Dquarkus.http.port=8380 \
-Dquarkus.log.category.\\\"org.kie.kogito.taskassigning\\\".level=DEBUG \
-Dquarkus.log.category.\\\"org.kie.kogito.taskassigning\\\".min-level=TRACE \
-Dquarkus.log.category.\\\"org.kie.kogito.taskassigning.service.messaging.ReactiveMessagingEventConsumer\\\".level=TRACE \
-Dquarkus.log.console.enable=true \
-Dquarkus.log.file.enable=true \
-Dquarkus.log.file.path=./task-assigning-service-extension.log \
-Dkogito.task-assigning.publish-window-size=2 \
-Dkogito.task-assigning.user-service-sync-interval=PT2H \
-Dkogito.task-assigning.improve-solution-on-background-duration=PT0.500S \
-jar target/task-assigning-service-extension-2.0.0-SNAPSHOT-runner.jar"

eval java $COMMAND

