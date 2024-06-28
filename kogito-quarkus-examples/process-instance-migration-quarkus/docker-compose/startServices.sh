#!/bin/sh

PROFILE="example"

echo "Script requires your Kogito Example to be compiled"

PROJECT_VERSION=$(cd ../ && mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

echo "Project version: ${PROJECT_VERSION}"

if [[ $PROJECT_VERSION == *SNAPSHOT ]];
then
  KOGITO_VERSION="latest"
else
  KOGITO_VERSION=${PROJECT_VERSION%.*}
fi

if [ -n "$1" ]; then
  if [[ ("$1" == "infra") || ("$1" == "example")]];
  then
    PROFILE="$1"
  else
    echo "Unknown docker profile '$1'. The supported profiles are:"
    echo "* 'infra': Use this profile to start only the minimum infrastructure to run the example (postgresql, data-index & jobs-service)."
    echo "* 'example': Use this profile to start the example infrastructure and the kogito-example service. Requires the example to be compiled using the 'container' profile (-Pcontainer)"
    exit 1;
  fi
fi

echo "Kogito Image version: ${KOGITO_VERSION}"
echo "KOGITO_VERSION=${KOGITO_VERSION}" > ".env"
echo "COMPOSE_PROFILES='${PROFILE}'" >> ".env"

if [ "$(uname)" == "Darwin" ]; then
   echo "DOCKER_GATEWAY_HOST=kubernetes.docker.internal" >> ".env"
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
   echo "DOCKER_GATEWAY_HOST=172.17.0.1" >> ".env"
fi

docker compose up