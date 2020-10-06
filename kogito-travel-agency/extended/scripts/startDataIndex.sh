#!/bin/sh
echo "Script requires your Kogito Travel Agency and Visas projects to be compiled"

PROJECT_VERSION=$(cd ../ && mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

echo "Project version: ${PROJECT_VERSION}"

DATA_INDEX_VERSION=${PROJECT_VERSION}

PERSISTENCE_FOLDER=target/classes/persistence
DATA_INDEX_RUNNER="https://repository.jboss.org/nexus/service/local/artifact/maven/content?r=public&g=org.kie.kogito&a=data-index-service-infinispan&v=${DATA_INDEX_VERSION}&c=runner"

KOGITO_TRAVEL_AGENCY_PERSISTENCE=../travels/target/classes/persistence
KOGITO_VISAS_PERSISTENCE=../visas/target/classes/persistence

mkdir -p $PERSISTENCE_FOLDER

if [ -d "$KOGITO_TRAVEL_AGENCY_PERSISTENCE" ]
then
    cp $KOGITO_TRAVEL_AGENCY_PERSISTENCE/*.proto $PERSISTENCE_FOLDER
else
    echo "$KOGITO_TRAVEL_AGENCY_PERSISTENCE does not exist. Have you compiled your Kogito Travel Agency project?"
    exit 1
fi

if [ -d "$KOGITO_VISAS_PERSISTENCE" ]
then
    cp $KOGITO_VISAS_PERSISTENCE/*.proto $PERSISTENCE_FOLDER
else
    echo "$KOGITO_VISAS_PERSISTENCE does not exist. Have you compiled your Kogito Visas project?"
    exit 1
fi

#[ ! -d ${PERSISTENCE_FOLDER} ] && echo "Persistence folder is missing. Make sure that your project was compiled" && exit 0

#wget -nc https://repository.jboss.org/org/kie/kogito/data-index-service-infinispan/${DATA_INDEX_VERSION}/data-index-service-infinispan-${DATA_INDEX_VERSION}-runner.jar
wget -nc -O data-index-service-infinispan-${DATA_INDEX_VERSION}-runner.jar ${DATA_INDEX_RUNNER}
cp -rf ${PERSISTENCE_FOLDER} persistence
java -jar  -Dkogito.protobuf.folder=`pwd`/persistence data-index-service-infinispan-${DATA_INDEX_VERSION}-runner.jar
