#!/bin/bash

action=$1

if [ "${action}" == "uninstall" ]; then
  echo "*** uninstalling infinispan"
  helm uninstall infinispan -n $(getProjectName)
  oc delete pvc,secret --selector clusterName=infinispan -n $(getProjectName)

elif [ "${action}" == "install" ]; then
  echo "*** installing infinispan"
  helm repo add openshift-helm-charts https://charts.openshift.io/
  helm install infinispan openshift-helm-charts/infinispan-infinispan --version "0.2.0" -f infinispan-values.yaml -n $(getProjectName) $(dryRun "Helm")

else
  echo "*** no such action: $action"
fi