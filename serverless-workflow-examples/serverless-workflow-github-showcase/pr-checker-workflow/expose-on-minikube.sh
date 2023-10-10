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

route=$(kubectl get routes -l receive-adapter=github -o jsonpath="{.items[*].metadata.name}" -n kogito-github)
svc=$(kubectl get svc -l serving.knative.dev/service="${route}",networking.internal.knative.dev/serviceType=Public -o jsonpath="{.items[*].metadata.name}" -n kogito-github)

cp ./kubernetes/local/patch-virtual-service.yaml.tpl ./kubernetes/local/patch-virtual-service.yaml
sed -i "s,{EVENT_LISTENER_SVC},${svc},g" ./kubernetes/local/patch-virtual-service.yaml

kubectl apply -k kubernetes/local

CURR_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
# shellcheck source=../scripts/add-route-to-hosts.sh
source "${CURR_DIR}/../scripts/add-route-to-hosts.sh" "${route}"
