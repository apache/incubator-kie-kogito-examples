#!/bin/sh
# Helper script to remove any cache from previous build

docker-compose stop
docker-compose rm
docker rmi kogito/outbox/quarkus/sidecar
docker rmi org.kie.kogito.examples/process-outbox-mongodb-quarkus:1.0
docker rmi kogito/outbox/quarkus/mongodb
docker system prune