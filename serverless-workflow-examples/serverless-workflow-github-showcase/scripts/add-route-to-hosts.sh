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

# see: https://knative.dev/docs/serving/using-a-custom-domain/#local-dns-setup

ROUTE_NAME=$1
if [ -z "${ROUTE_NAME}" ]; then
  echo "---> Please inform the route name. You can get it by running 'kubectl get route ROUTE -n NAMESPACE'"
  exit
fi

GATEWAY_IP=$(kubectl get svc istio-ingressgateway --namespace istio-system --output jsonpath="{.status.loadBalancer.ingress[*]['ip']}")
if [ -z "${GATEWAY_IP}" ]; then
  echo "---> Failed to obtain Gateway IP. Have you run 'minikube tunnel' on a separate terminal?"
  exit
fi

echo "---> Trying to connect to ${GATEWAY_IP}"

if ! curl -m 3  -vv "${GATEWAY_IP}"; then
  echo "---> Failed to reach Istio Ingress endpoint. Have you run 'minikube tunnel' on a separate terminal?"
  exit
fi

DOMAIN_NAME=$(kubectl get route "${ROUTE_NAME}" --output jsonpath="{.status.url}" | cut -d'/' -f 3)
if [ -z "${DOMAIN_NAME}" ]; then
  echo "---> Route ${ROUTE_NAME} not valid. You can get it by running 'kubectl get route ROUTE -n NAMESPACE'"
  exit
fi

echo "---> Add the record of Gateway IP and Route domain name into file '/etc/hosts'"
echo -e "${GATEWAY_IP}\t${DOMAIN_NAME}" | sudo tee -a /etc/hosts
