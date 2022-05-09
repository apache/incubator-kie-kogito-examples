#!/bin/bash

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