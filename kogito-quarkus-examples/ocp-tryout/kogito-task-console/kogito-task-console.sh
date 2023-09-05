#!/bin/bash

action=$1

if [ "${action}" == "uninstall" ]; then
  echo "*** uninstalling task console"
  oc delete all,configmap --selector app=kogito-task-console -n $(getProjectName)

elif [ "${action}" == "install" ]; then
  echo "*** installing task console"
  oc new-app quay.io/kiegroup/kogito-task-console:2.44
  waitForPod kogito-task-console
  oc patch deployment kogito-task-console --patch "$(cat deployment-patch.yaml)" -n $(getProjectName) $(dryRun)
  waitForPod kogito-task-console
  oc expose service/kogito-task-console -n $(getProjectName) $(dryRun)
else
  echo "*** no such action: $action"
fi