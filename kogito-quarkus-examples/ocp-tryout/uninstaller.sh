#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#


source installer.properties
source common-functions.sh

action=uninstall

components=(SHARED_CONFIG KAFKA KEYCLOAK \
           KOGITO_DATA_INDEX KOGITO_JOBS_SERVICE \
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

  if [ -z $OCP_PROJECT ]; then
    echo "OCP_PROJECT property is not defined"
    exit 1
  else
    oc get "project/$OCP_PROJECT" > /dev/null 2>&1
    if [ "$?" != "0" ]; then
      echo "OCP_PROJECT $OCP_PROJECT does not exist"
      exit 1
    fi
  fi

  overrideEnvVariables

  echo "************* UNINSTALL START *****************"

  componentAction "${TEST_APP}" "testapp"

  dbType=""
  if [ "${POSTGRESQL}" == "Y" ]; then
    dbType="postgresql"
  fi

  componentAction "${KOGITO_DATA_INDEX}" "kogito-data-index" "${dbType}"
  componentAction "${KOGITO_MANAGEMENT_CONSOLE}" "kogito-management-console"
  componentAction "${KOGITO_TASK_CONSOLE}" "kogito-task-console"
  componentAction "${KOGITO_JOBS_SERVICE}" "kogito-jobs-service" "${dbType}"

  componentAction "${POSTGRESQL}" "postgresql"
  componentAction "${KAFKA}" "kafka"
  componentAction "${KEYCLOAK}" "keycloak"

  componentAction "${SHARED_CONFIG}" "kogito-shared"
}
rm -f uninstallLogs.log
touch uninstallLogs.log
uninstall |& tee uninstallLogs.log

exit 0






