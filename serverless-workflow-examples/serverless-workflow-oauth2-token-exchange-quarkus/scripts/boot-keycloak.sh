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

user="admin"
password="admin"
java_opts="-Dkeycloak.profile.feature.token_exchange=enabled -Dkeycloak.profile.feature.admin_fine_grained_authz=enabled -Dkeycloak.profile.feature.impersonation=enabled"
realm_path="$(pwd)/keycloak"

if ! test "${realm_path}"; then
  echo "--> Realm import file '${realm_path}' does not exist. Check your path and try again."
  exit 1
fi

# Start Keycloak in devmode, see: https://www.keycloak.org/server/containers
docker run -p 9090:8080 -e KEYCLOAK_ADMIN="${user}" -e KEYCLOAK_ADMIN_PASSWORD="${password}" -e JAVA_OPTS_APPEND="${java_opts}" \
        -v "${realm_path}":/opt/keycloak/data/import \
        quay.io/keycloak/keycloak:latest \
        start-dev --import-realm
