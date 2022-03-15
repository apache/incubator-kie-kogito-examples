#!/bin/bash

source installer.properties
source common-functions.sh

action=uninstall

components=(INFINISPAN KAFKA KEYCLOAK \
           SHARED_CONFIG KOGITO_DATA_INDEX KOGITO_MANAGEMENT_CONSOLE KOGITO_TASK_CONSOLE KOGITO_JOBS_SERVICE \
           TEST_APP)
# override the installer properties configuration if needed
function overrideEnvVariables(){
  if [ "${INSTALL_ALL}" == Y ]; then
    for comp in "${components[@]}"
    do
      export "${comp}"=Y
    done
  fi
}

function componentAction(){
  doComponent=$1
  component=$2
  extraVar=$3
  if [ "${doComponent}" == Y ]; then
    cd "${component}"
    source "${component}".sh "${action}" "${extraVar}"
    cd ..
  fi
}

function uninstall(){

  overrideEnvVariables

  componentAction "${TEST_APP}" "testapp"

  dbType=""
  if [ "${INFINISPAN}" == "Y" ]; then
    dbType="infinispan"
  fi

  componentAction "${KOGITO_DATA_INDEX}" "kogito-data-index" "${dbType}"
  componentAction "${KOGITO_MANAGEMENT_CONSOLE}" "kogito-management-console"
  componentAction "${KOGITO_TASK_CONSOLE}" "kogito-task-console"
  componentAction "${KOGITO_JOBS_SERVICE}" "kogito-jobs-service" "${dbType}"

  componentAction "${INFINISPAN}" "infinispan"
  componentAction "${KAFKA}" "kafka"
  componentAction "${KEYCLOAK}" "keycloak"

  componentAction "${SHARED_CONFIG}" "kogito-shared"
}
uninstall > uninstallLogs.txt 2>&1

exit 0






