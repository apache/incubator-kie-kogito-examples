#!/bin/bash

cd ../
mvn clean package

cd docker-compose/ 
cp ../target/generated-sources/kogito/dashboards/* grafana/provisioning/dashboards

sudo docker-compose build && sudo docker-compose up
