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


# firstly, any Kogito unrelated infrastructure like infinispan, kafka, etc. is installed
# secondly, any Kogito services like data-index, management console, etc. is installed
# thirdly, the application to try out is installed

source installer.properties
source common-functions.sh

action=install

components=(SHARED_CONFIG INFINISPAN KAFKA KEYCLOAK \
           KOGITO_DATA_INDEX KOGITO_MANAGEMENT_CONSOLE KOGITO_TASK_CONSOLE KOGITO_JOBS_SERVICE \
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

# find all the components to install for logging purposes
# input: array of component names
function componentsToInstall(){
  local -n compArray=$1
  ic=""
  for comp in "${compArray[@]}"
  do
    if [ "${!comp}" == 'Y' ]; then
      ic+=$comp" *** "
    fi
  done
  echo "${ic}";
}

# install one component
# input:
# action: [install|uninstall];
# doComponent: [Y|N], value of the components env variable
# component: [component folder as well as execute shell script name]
# extraVar: any extra data needed for a specific component
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

# install all components
function install(){
  if [ -z $KOGITO_VERSION ]; then
    echo "KOGITO_VERSION property is not defined"
    exit 1
  fi
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

  echo "************* INSTALLATION START *****************"
  echo "**************************************************"
  echo -e "*** Kogito version:\t\t\t$KOGITO_VERSION"
  echo -e "*** OCP project name:\t\t\t$OCP_PROJECT"
  echo -e "*** Install all?\t\t\t$INSTALL_ALL"
  echo -e "*** Is Dry run?\t\t\t\t$DRY_RUN"
  echo -e "*** Installing components:\t\t$(componentsToInstall components)";
  echo "**************************************************"

  componentAction "${SHARED_CONFIG}" "kogito-shared"

  componentAction "${INFINISPAN}" "infinispan"
  componentAction "${KAFKA}" "kafka"
  componentAction "${KEYCLOAK}" "keycloak"

  dbType="infinispan"

  componentAction "${KOGITO_DATA_INDEX}" "kogito-data-index" "${dbType}"
  componentAction "${KOGITO_MANAGEMENT_CONSOLE}" "kogito-management-console"
  componentAction "${KOGITO_TASK_CONSOLE}" "kogito-task-console"
  componentAction "${KOGITO_JOBS_SERVICE}" "kogito-jobs-service" "${dbType}"

  componentAction "${TEST_APP}" "testapp"

  echo "************* INSTALLATION END *******************"
}
rm -f installLogs.log
touch installLogs.log
install |& tee installLogs.log

exit 0
