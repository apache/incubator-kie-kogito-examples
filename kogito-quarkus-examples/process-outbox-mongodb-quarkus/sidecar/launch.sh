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

set -euxo pipefail

until mongo -u "$MONGODB_USER" -p "$MONGODB_PASSWORD" --host "$MONGODB_RS"/"$MONGODB_HOST" admin --eval "print(\"waited for connection\")"; do
  echo "Wait for MongoDB"
  sleep 1
done

until [ "$(curl --output /dev/null --silent --head -w ''%{http_code}'' "$CONNECT_HOST")" == "200" ]; do
  echo "Wait for Debezium"
  sleep 1
done

echo "Start MongoDB connector"

curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" "$CONNECT_HOST"/connectors/ -d @- <<-EOF
  {
    "name": "kogito-connector",
    "config": {
      "connector.class" : "io.debezium.connector.mongodb.MongoDbConnector",
      "mongodb.hosts" : "$MONGODB_RS/$MONGODB_HOST",
      "mongodb.name" : "dbserver1",
      "mongodb.user" : "$MONGODB_USER",
      "mongodb.password" : "$MONGODB_PASSWORD",
      "database.include" : "kogito",
      "database.history.kafka.bootstrap.servers" : "$KAFKA_HOST",
      "key.converter": "org.apache.kafka.connect.json.JsonConverter",
      "key.converter.schemas.enable": "false",
      "value.converter": "org.apache.kafka.connect.json.JsonConverter",
      "value.converter.schemas.enable": "false",
      "collection.include.list": "kogito.kogitoprocessinstancesevents,kogito.kogitousertaskinstancesevents,kogito.kogitovariablesevents",
      "transforms": "unwrap,reroute",
      "transforms.unwrap.type": "io.debezium.connector.mongodb.transforms.ExtractNewDocumentState",
      "transforms.unwrap.array.encoding": "array",
      "transforms.unwrap.drop.tombstones": "false",
      "transforms.unwrap.delete.handling.mode": "drop",
      "transforms.unwrap.operation.header": "false",
      "transforms.reroute.type": "io.debezium.transforms.ByLogicalTableRouter",
      "transforms.reroute.topic.regex": "(.*)kogito(.*)events(.*)",
      "transforms.reroute.topic.replacement": "kogito-\$2-events",
      "transforms.reroute.key.enforce.uniqueness": "false",
      "skipped.operations": "u,d",
      "tombstones.on.delete": "false"
    }
  }
EOF

echo "MongoDB connector started"
