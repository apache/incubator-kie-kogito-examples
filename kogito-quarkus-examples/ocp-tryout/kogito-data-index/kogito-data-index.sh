#!/bin/bash

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
  oc new-app quay.io/kiegroup/kogito-data-index-"${type}":2.44
  waitForPod kogito-data-index
  oc patch deployment kogito-data-index-"${type}" --patch "$(cat deployment-patch-"${type}".yaml)" -n $(getProjectName) $(dryRun)
  waitForPod kogito-data-index
  oc expose service/kogito-data-index-"${type}" -n $(getProjectName) $(dryRun)

else
  echo "*** no such action: $action"
fi