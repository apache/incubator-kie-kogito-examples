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
  echo "*** uninstalling jobs service"
  oc delete all,configmap --selector app=kogito-jobs-service-${type} -n $(getProjectName)

elif [ "${action}" == "install" ]; then
  echo "*** installing jobs service"
  oc new-app docker.io/apache/incubator-kie-kogito-jobs-service-${type}:${KOGITO_VERSION} -n $(getProjectName) $(dryRun "NewApp")
  waitForPod kogito-jobs-service
  oc patch deployment kogito-jobs-service-${type} --patch "$(cat deployment-patch.yaml)" -n $(getProjectName) $(dryRun)
  waitForPod kogito-jobs-service
  oc expose service/kogito-jobs-service-${type} -n $(getProjectName) $(dryRun)
else
  echo "*** no such action: $action"
fi