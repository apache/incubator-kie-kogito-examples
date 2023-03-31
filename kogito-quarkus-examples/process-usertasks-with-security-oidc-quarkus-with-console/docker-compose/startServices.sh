#!/bin/sh

echo "Script requires your Kogito Quickstart to be compiled with mvn clean install -Pcontainer"

PROJECT_VERSION=$(cd ../ && mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

echo "Project version: ${PROJECT_VERSION}"

if [[ $PROJECT_VERSION == *SNAPSHOT ]];
then
  KOGITO_VERSION="latest"
else
  KOGITO_VERSION=${PROJECT_VERSION%.*}
fi

echo "Kogito Image version: ${KOGITO_VERSION}"
echo "KOGITO_VERSION=${KOGITO_VERSION}" > ".env"

if [ "$(uname)" == "Darwin" ]; then
   echo "DOCKER_GATEWAY_HOST=kubernetes.docker.internal" >> ".env"
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
   echo "DOCKER_GATEWAY_HOST=172.17.0.1" >> ".env"
fi

PERSISTENCE_FOLDER=./persistence
KOGITO_EXAMPLE_PERSISTENCE=../target/classes/META-INF/resources/persistence/protobuf

rm -rf $PERSISTENCE_FOLDER

mkdir -p $PERSISTENCE_FOLDER

if [ -d "$KOGITO_EXAMPLE_PERSISTENCE" ]
then
    cp $KOGITO_EXAMPLE_PERSISTENCE/*.proto $PERSISTENCE_FOLDER/
else
    echo "$KOGITO_EXAMPLE_PERSISTENCE does not exist. Have you compiled the project?"
    exit 1
fi

SVG_FOLDER=./svg

KOGITO_EXAMPLE_SVG_FOLDER=../target/classes/META-INF/processSVG

mkdir -p $SVG_FOLDER

if [ -d "$KOGITO_EXAMPLE_SVG_FOLDER" ]
then
    cp $KOGITO_EXAMPLE_SVG_FOLDER/*.svg $SVG_FOLDER
else
    echo "$KOGITO_EXAMPLE_SVG_FOLDER does not exist. Have you compiled the project?"
    exit 1
fi

docker-compose up