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


action=$1

# NOTE: if need to update kogito_realm.json, edit content of kogito-realm-orig.json here
function updateClientRedirectUrls(){
  mngConsole=\"http://kogito-management-console-$(getProjectName).$(getClusterAppsHostname)/*\"
  taskConsole=\"http://kogito-task-console-$(getProjectName).$(getClusterAppsHostname)/*\"
  additionalRedirectUris=["${mngConsole}","${taskConsole}"]
  (jq '(.clients[] | select(.clientId=="kogito-console-quarkus") | .redirectUris) |= . + '${additionalRedirectUris} kogito-realm-orig.json) > kogito-realm.json
}
updateClientRedirectUrls

if [ "${action}" == "uninstall" ]; then
  echo "*** uninstalling keycloak"
  oc delete all,configmap --selector app=keycloak -n $(getProjectName)

elif [ "${action}" == "install" ]; then
  echo "*** installing keycloak"
  oc create configmap keycloak-config --from-file=./kogito-realm.json -o yaml --dry-run=client | \
    oc label -f- --dry-run=client -o yaml --local=true app=keycloak | \
    oc apply -f- -n $(getProjectName) $(dryRun)
  oc new-app quay.io/keycloak/keycloak:15.0.2 -n $(getProjectName) $(dryRun "NewApp")
  waitForPod keycloak
  oc patch deployment keycloak --patch "$(cat deployment-patch.yaml)" -n $(getProjectName) $(dryRun)
  waitForPod keycloak
  oc expose service/keycloak -n $(getProjectName) $(dryRun)

else
  echo "*** no such action: $action"
fi