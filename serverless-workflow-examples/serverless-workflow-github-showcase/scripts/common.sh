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

BUILDER=podman

function check_binaries() {
  local return_code=0

  if ! command -v kubectl &>/dev/null; then
    echo "---> kubectl not found, please install it to run this script"
    return_code=1
  fi

  if ! command -v podman &>/dev/null; then
    echo "---> podman not found, setting default builder to docker"
    BUILDER=docker
    if ! command -v docker &>/dev/null; then
      echo "---> docker not found, please install it to run this script" && return_code=1
    fi
  fi

  return ${return_code}
}

function build_push_image() {
  image_ns=$1
  image_name=$2
  # defining image tag
  image_tag="quay.io/${image_ns}/${image_name}:latest"

  echo "---> Building and pushing image using tag ${image_tag}"
  # build image
  if [ "${BUILDER}" == "docker" ]; then
    docker build --tag "${image_tag}" .
    docker push "${image_tag}"
  else
    podman build --tag "${image_tag}" .
    podman push "${image_tag}"
  fi
}
