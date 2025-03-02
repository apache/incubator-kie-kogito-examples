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
  echo "*** uninstalling postgresql"
  helm uninstall postgresql -n $(getProjectName)
  oc delete pvc,secret --selector clusterName=postgresql -n $(getProjectName)

elif [ "${action}" == "install" ]; then
  echo "*** installing postgresql"
  helm repo add openshift-helm-charts https://charts.openshift.io/
  helm install postgresql openshift-helm-charts/redhat-postgresql-persistent --version "0.0.3" -f postgresql-values.yaml -n $(getProjectName) $(dryRun "Helm")

else
  echo "*** no such action: $action"
fi