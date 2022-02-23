#!/bin/sh
# Helper script to remove any cache from previous build

docker-compose stop
docker-compose rm
docker rmi kogito/outbox/springboot/sidecar
docker rmi kogito/outbox/springboot/runtime
docker rmi kogito/outbox/springboot/mongodb
docker system prune