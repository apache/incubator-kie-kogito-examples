#!/bin/sh
echo "Script requires your Kogito Travel Agency and Visas projects to be compiled"

DATA_INDEX_VERSION=0.8.1
PERSISTENCE_FOLDER=target/classes/persistence
DATA_INDEX_RUNNER=https://search.maven.org/remotecontent?filepath=org/kie/kogito/data-index-service/${DATA_INDEX_VERSION}/data-index-service-${DATA_INDEX_VERSION}-runner.jar

KOGITO_TRAVEL_AGENCY_PERSISTENCE=../08-kogito-travel-agency/target/classes/persistence
KOGITO_VISAS_PERSISTENCE=../08-kogito-visas/target/classes/persistence

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

#wget -nc https://repo2.maven.org/maven2/org/kie/kogito/data-index-service/${DATA_INDEX_VERSION}/data-index-service-${DATA_INDEX_VERSION}-runner.jar
wget -nc -O data-index-service-${DATA_INDEX_VERSION}-runner.jar ${DATA_INDEX_RUNNER}
cp -rf ${PERSISTENCE_FOLDER} persistence
java -jar  -Dkogito.protobuf.folder=`pwd`/persistence data-index-service-${DATA_INDEX_VERSION}-runner.jar
