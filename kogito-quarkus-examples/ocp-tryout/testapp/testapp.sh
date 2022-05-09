#!/bin/bash

action=$1

function getReleaseName(){
  releaseName=$(echo "$1" | awk -F "." '{ print $1 }')
  echo $releaseName
}

if [ ${action} == 'install' ]; then
  toInstall=$(ls ./apps)
  for valuesFile in ${toInstall}
  do
    releaseName=$(getReleaseName "${valuesFile}")
    echo "*** installing test application $releaseName"
    helm install $releaseName ../tryout-kogito-app --values ./apps/${valuesFile} -n $(getProjectName) $(dryRun "Helm")
  done

elif [ ${action} == 'uninstall' ]; then
  toInstall=$(ls ./apps)
  for valuesFile in ${toInstall}
  do
    releaseName=$(getReleaseName "${valuesFile}")
    echo "*** uninstalling test application $releaseNamee"
    helm uninstall $releaseName -n $(getProjectName)
  done
fi

