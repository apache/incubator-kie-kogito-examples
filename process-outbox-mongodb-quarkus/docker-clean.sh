#!/bin/sh
# Helper script to remove any cache from previous build

docker-compose stop
docker-compose rm
docker rmi kogito/outbox/quarkus/sidecar
docker rmi kogito/outbox/quarkus/runtime
docker rmi kogito/outbox/quarkus/mongodb
docker system prune