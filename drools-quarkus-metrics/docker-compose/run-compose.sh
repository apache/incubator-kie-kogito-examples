#!/bin/bash

cd ../
mvn clean package

cd docker-compose/ 
cp /tmp/dashboard-endpoint* grafana/provisioning/dashboards

sudo docker-compose build && sudo docker-compose up
