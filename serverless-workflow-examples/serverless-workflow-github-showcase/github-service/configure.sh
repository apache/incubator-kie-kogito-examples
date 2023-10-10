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

QUAY_NAMESPACE=$1
APP_ID=$2
INSTALLATION_ID=$3
DER_FILE=$4

CURR_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
# shellcheck source=../scripts/common.sh
source "${CURR_DIR}/../scripts/common.sh"

function print_usage() {
  echo "---> Script to deploy the GitHub service to the Kubernetes cluster. Usage:"
  echo "---> ./deploy-kubernetes.sh QUAY_NAMESPACE GITHUB_APP_ID INSTALLATION_ID PATH_FOR_DER_FILE"
  echo "---> Example: "
  echo "---> ./deploy-kubernetes.sh namespace 12345 56788 /path/to/the/private_key.der"
}

function verify_input() {
  local return_code=0
  if [ -z "${QUAY_NAMESPACE}" ]; then
    echo "---> Quay namespace not set"
    return_code=1
  fi

  if [ -z "${APP_ID}" ]; then
    echo "---> GitHub Application ID not set"
    return_code=1
  fi

  if [ -z "${INSTALLATION_ID}" ]; then
    echo "---> GitHub Application Installation ID not set"
    return_code=1
  fi

  if [ -z "${DER_FILE}" ]; then
    echo "---> DER file not set"
    return_code=1
  elif [ ! -f "${DER_FILE}" ]; then
    echo "---> DER file ${DER_FILE} does not exist, make sure that the file informed is correct"
    return_code=1
  fi

  return ${return_code}
}

function apply_vars() {
  cp ./kubernetes/apply_image_ns.yaml.tpl ./kubernetes/apply_image_ns.yaml
  cp ./kubernetes/github-app.env.tpl ./kubernetes/github-app.env

  sed -i "s/{QUAY_NAMESPACE}/${QUAY_NAMESPACE}/g" ./kubernetes/apply_image_ns.yaml
  sed -i "s/{APP_ID}/${APP_ID}/g" ./kubernetes/github-app.env
  sed -i "s/{INSTALLATION_ID}/${INSTALLATION_ID}/g" ./kubernetes/github-app.env
}

if ! verify_input; then
  print_usage
  exit
fi

if ! check_binaries; then
  echo "---> exiting installation script, not all required binaries have been found in your system"
  exit
fi

apply_vars

# copy DER file
cp "${DER_FILE}" ./kubernetes/github-app-key.der
