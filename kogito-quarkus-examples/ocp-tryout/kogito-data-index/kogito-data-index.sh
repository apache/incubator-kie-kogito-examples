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
type=$2

if [ "${action}" == "uninstall" ]; then
  echo "*** uninstalling data-index ${type}"
  oc delete all,configmap --selector app=kogito-data-index-"${type}" -n $(getProjectName)

elif [ "${action}" == "install" ]; then
  echo "*** installing data-index ${type}"
  # at least empty protobuf folder needs to exist
  if [ ! -d ../testapp/protobuf ]; then
    mkdir ../testapp/protobuf
  fi
  # cannot add a label directly to a config map create command => workaround:
  # create a configmap yaml locally -> update label on that locally => pipe into server "create cm from yaml" command
  oc create configmap data-index-config --from-file=../testapp/protobuf -o yaml --dry-run=client | \
    oc label -f- --dry-run=client -o yaml --local=true app=kogito-data-index-"${type}" | \
    oc apply -f- -n $(getProjectName) $(dryRun)
<<<<<<< HEAD
  oc new-app quay.io/kiegroup/kogito-data-index-"${type}":10.0
=======
  oc new-app docker.io/apache/incubator-kie-kogito-data-index-"${type}":"${KOGITO_VERSION}" -n $(getProjectName) $(dryRun "NewApp")
>>>>>>> e5e150f18 (Fix kie-issues #1217 Remove infinispan based images from a few examples (#1927))
  waitForPod kogito-data-index
  oc patch deployment kogito-data-index-"${type}" --patch "$(cat deployment-patch-"${type}".yaml)" -n $(getProjectName) $(dryRun)
  waitForPod kogito-data-index
  oc expose service/kogito-data-index-"${type}" -n $(getProjectName) $(dryRun)

else
  echo "*** no such action: $action"
fi