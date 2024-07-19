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


function waitForPod(){
  if [ "$DRY_RUN" == N ]; then
    podNameStart=$1
    res=$(oc get pod -n $(getProjectName) | grep "${podNameStart}" | awk '{print $3}')
    until [ "${res}" == "Running" ]
    do
      echo waiting for "${podNameStart}"
      sleep 2
      res=$(oc get pod  -n $(getProjectName) | grep "${podNameStart}" | awk '{print $3}')
      echo  "pod status is:"$res
    done
  fi
}

function getProjectName(){
#  current_project_name=$(oc project -q)
#  echo "${current_project_name}"
  echo "$OCP_PROJECT"
}

function getClusterAppsHostname(){
   # get apps cluster server hostname - get current contexts api cluster name
    current_context_clustername=$(oc config current-context |  cut -d'/' -f2)
    # use the cluster name to find the cluster api url inside a possible list of clusters
    current_context_clusterurl_api=$(oc config view -o jsonpath='{"Cluster name\tServer\n"}{range .clusters[*]}{.name}{"\t"}{.cluster.server}{"\n"}{end}' | grep "${current_context_clustername}" | awk '{print $2}')
    # only get hostname
    current_context_clusterurl_api=${current_context_clusterurl_api%:*}
    current_context_clusterurl_api=${current_context_clusterurl_api##*/}
    # replace api with apps
    current_context_clusterhost_apps="apps.${current_context_clusterurl_api#*.}"
    echo "${current_context_clusterhost_apps}"
}

function dryRun(){
  commandType=$1
  if [ -z $commandType ]; then
    commandType="default"
  fi
  if [ "$DRY_RUN" == Y ]; then
    if [ $commandType == "NewApp" ]; then
      echo "--dry-run=true -o yaml"
    elif [ $commandType == "Helm" ]; then
      echo "--debug --dry-run -o yaml"
    else
      echo "--dry-run=client -o yaml"
    fi
  fi
}