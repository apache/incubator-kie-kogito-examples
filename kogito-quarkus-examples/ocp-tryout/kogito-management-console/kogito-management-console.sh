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

if [ "${action}" == "uninstall" ]; then
  echo "*** uninstalling management console"
  oc delete all,configmap --selector app=kogito-management-console -n $(getProjectName)

elif [ "${action}" == "install" ]; then
  echo "*** installing management console"
  # at least empty svg folder needs to exist
  if [ ! -d ../testapp/svg ]; then
    mkdir ../testapp/svg
  fi
  # cannot add a label directly to a config map create command => workaround:
  # create a configmap yaml locally -> update label on that locally => pipe into server "create cm from yaml" command
  oc create configmap kogito-management-config --from-file=../testapp/svg -o yaml --dry-run=client | \
    oc label -f- --dry-run=client -o yaml --local=true app=kogito-management-console | \
    oc apply -f- -n $(getProjectName) $(dryRun)
  oc new-app quay.io/kiegroup/kogito-management-console:1.43
  waitForPod kogito-management-console
  oc patch deployment kogito-management-console --patch "$(cat deployment-patch.yaml)" -n $(getProjectName) $(dryRun)
  waitForPod kogito-management-console
  oc expose service/kogito-management-console -n $(getProjectName) $(dryRun)

else
  echo "*** no such action: $action"
fi