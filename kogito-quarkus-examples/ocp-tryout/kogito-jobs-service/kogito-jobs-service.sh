#!/bin/bash

action=$1
type=$2

if [ "${action}" == "uninstall" ]; then
  echo "*** uninstalling jobs service"
  oc delete all,configmap --selector app=kogito-jobs-service-${type} -n $(getProjectName)

elif [ "${action}" == "install" ]; then
  echo "*** installing jobs service"
  oc new-app quay.io/kiegroup/kogito-jobs-service-${type}:1.44
  waitForPod kogito-jobs-service
  oc patch deployment kogito-jobs-service-${type} --patch "$(cat deployment-patch.yaml)" -n $(getProjectName) $(dryRun)
  waitForPod kogito-jobs-service
  oc expose service/kogito-jobs-service-${type} -n $(getProjectName) $(dryRun)
else
  echo "*** no such action: $action"
fi