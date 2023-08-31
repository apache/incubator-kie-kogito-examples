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


source ../common-functions.sh

action=$1

if [ "${action}" == "uninstall" ]; then
  echo "*** uninstalling kogito-shared"
  oc delete -f kogito-configs.yaml -n $(getProjectName)

elif [ "${action}" == "install" ]; then
  echo "*** installing kogito-share"

  sed 's@${project_name}@'$(getProjectName)'@g;s@${apps_cluster_host}@'$(getClusterAppsHostname)'@g' \
        ./kogito-configs.yaml > ./kogito-configs-updated.yaml
  oc create -f kogito-configs-updated.yaml -n $(getProjectName) $(dryRun)
  rm kogito-configs-updated.yaml
fi