#!/bin/bash

cd ../
mvn clean package

cd docker-compose/ 
cp ../target/generated-resources/kogito/dashboards/* grafana/provisioning/dashboards

docker-compose build && docker-compose up
