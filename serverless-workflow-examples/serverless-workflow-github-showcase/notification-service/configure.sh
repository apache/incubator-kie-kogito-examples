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
SLACK_WEBHOOK=$2

CURR_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
# shellcheck source=../scripts/common.sh
source "${CURR_DIR}/../scripts/common.sh"

function print_usage() {
  echo "---> Script to deploy the Notification service to the Kubernetes cluster. Usage:"
  echo "---> ./deploy-kubernetes.sh QUAY_NAMESPACE SLACK_WEBHOOK"
  echo "---> Example: "
  echo "---> ./deploy-kubernetes.sh namespace "
}

function verify_input() {
  local return_code=0
  if [ -z "${QUAY_NAMESPACE}" ]; then
    echo "---> Quay namespace not set"
    return_code=1
  fi

  if [ -z "${SLACK_WEBHOOK}" ]; then
    echo "---> Slack WebHook ID not set"
    return_code=1
  fi

  return ${return_code}
}

function apply_vars() {
  cp ./kubernetes/apply_image_ns.yaml.tpl ./kubernetes/apply_image_ns.yaml
  cp ./kubernetes/slack.env.tpl ./kubernetes/slack.env

  sed -i "s/{QUAY_NAMESPACE}/${QUAY_NAMESPACE}/g" ./kubernetes/apply_image_ns.yaml
  sed -i "s,{SLACK_WEBHOOK},${SLACK_WEBHOOK},g" ./kubernetes/slack.env
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