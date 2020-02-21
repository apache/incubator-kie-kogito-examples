#!/bin/bash

metricsComposeDir=$(pwd)

cd /tmp/
git clone https://github.com/r00ta/jgrafana.git
cd jgrafana
mvn clean install

cd ..
git clone https://github.com/r00ta/kogito-runtimes.git
cd kogito-runtimes
git checkout system-metrics
mvn clean install -DskipTests


cd $metricsComposeDir
