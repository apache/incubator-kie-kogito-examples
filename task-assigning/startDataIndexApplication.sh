#!/bin/bash

java -Dquarkus.infinispan-client.use-auth=true -Dquarkus.infinispan-client.auth-username=myuser -Dquarkus.infinispan-client.auth-password=qwer1234! -Dkogito.protobuf.folder=`pwd`/PROTOS -Ddebug=5008 -jar data-index-service-infinispan-2.0.0-SNAPSHOT-runner.jar


