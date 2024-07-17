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
  echo "*** uninstalling task console"
  oc delete all,configmap --selector app=kogito-task-console -n $(getProjectName)

elif [ "${action}" == "install" ]; then
  echo "*** installing task console"
  oc new-app quay.io/kiegroup/kogito-task-console:10.0
  waitForPod kogito-task-console
  oc patch deployment kogito-task-console --patch "$(cat deployment-patch.yaml)" -n $(getProjectName) $(dryRun)
  waitForPod kogito-task-console
  oc expose service/kogito-task-console -n $(getProjectName) $(dryRun)
else
  echo "*** no such action: $action"
fi