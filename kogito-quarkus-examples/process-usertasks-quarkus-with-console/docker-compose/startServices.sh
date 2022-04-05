#!/bin/sh

DB="postgresql"

if [ -n "$1" ]; then
  if [[ "$1" == "postgresql"  || "$1" == "infinispan" ]];
  then
    DB="$1"
  else
   echo "Usage: By default postgresql environments is started if no argument is provided"
   echo "     start POSTGRESQL docker-compose running: ./startServices.sh postgresql or just ./startServices.sh "
   echo "     start INFINISPAN docker-compose running: ./startServices.sh infinispan "
   exit 1
  fi
fi
echo "Script requires your Kogito Quickstart to be compiled with the right profile: ../mvn clean install -DskipTests -P$DB"

PROJECT_VERSION=$(cd ../ && mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
KEYCLOAK_VERSION=$(cd ../ && mvn help:evaluate -Dexpression=version.org.keycloak -q -DforceStdout)

echo "Project version: ${PROJECT_VERSION}"

if [[ $PROJECT_VERSION == *SNAPSHOT ]];
then
  KOGITO_VERSION="latest"
else
  KOGITO_VERSION=${PROJECT_VERSION%.*}
fi

echo "Kogito Image version: ${KOGITO_VERSION}"
echo "KOGITO_VERSION=${KOGITO_VERSION}" > ".env"
echo "KEYCLOAK_VERSION=${KEYCLOAK_VERSION}" >> ".env"

if [ "$(uname)" == "Darwin" ]; then
   echo "DOCKER_GATEWAY_HOST=kubernetes.docker.internal" >> ".env"
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
   echo "DOCKER_GATEWAY_HOST=172.17.0.1" >> ".env"
fi

if [ "$1" == "infinispan" ];
then
  PERSISTENCE_FOLDER=./persistence
  KOGITO_EXAMPLE_PERSISTENCE=../target/classes/META-INF/resources/persistence/protobuf

  rm -rf $PERSISTENCE_FOLDER

  mkdir -p $PERSISTENCE_FOLDER

  if [ -d "$KOGITO_EXAMPLE_PERSISTENCE" ]
  then
    cp $KOGITO_EXAMPLE_PERSISTENCE/*.proto $PERSISTENCE_FOLDER/
  else
    echo "$KOGITO_EXAMPLE_PERSISTENCE does not exist. Have you compiled the project? mvn clean install -DskipTests -P$DB"
    exit 1
  fi
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

docker-compose -f docker-compose-$DB.yml up