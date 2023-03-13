#!/bin/sh
# Helper script to remove any cache from previous build

docker-compose stop
docker-compose rm
docker rmi kogito/outbox/springboot/sidecar
docker rmi org.kie.kogito.examples/process-outbox-mongodb-springboot:1.0
docker rmi kogito/outbox/springboot/mongodb
docker system prune