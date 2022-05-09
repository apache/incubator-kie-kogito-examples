#!/bin/bash

action=$1

if [ "${action}" == "uninstall" ]; then
  echo "*** uninstalling kafka"
  helm uninstall kafka -n $(getProjectName)
  oc delete pvc --selector app.kubernetes.io/instance=kafka -n $(getProjectName)

elif [ "${action}" == "install" ]; then
  echo "*** installing kafka"
  helm repo add bitnami https://charts.bitnami.com/bitnami
  helm install kafka bitnami/kafka --version "14.4.3" -f kafka-values.yaml -n $(getProjectName) $(dryRun "Helm")

else
  echo "*** no such action: $action"
fi