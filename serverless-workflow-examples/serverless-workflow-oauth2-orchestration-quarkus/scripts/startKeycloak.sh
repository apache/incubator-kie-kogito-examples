#!/bin/bash

export REALM_FILE_VOLUME=$PWD/../docker-compose/keycloak/kogito-realm.json:/tmp/kogito-realm.json

docker run -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin  -e KEYCLOAK_IMPORT=/tmp/kogito-realm.json -v $REALM_FILE_VOLUME -p 8281:8080  quay.io/keycloak/keycloak:legacy

