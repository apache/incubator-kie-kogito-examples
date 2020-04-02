#!/bin/bash

cd ../
mvn clean package

cd docker-compose/ 
cp ../target/resources/dashboards/* grafana/provisioning/dashboards

docker-compose build && docker-compose up
