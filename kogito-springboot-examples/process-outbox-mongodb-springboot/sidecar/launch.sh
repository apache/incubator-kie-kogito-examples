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
