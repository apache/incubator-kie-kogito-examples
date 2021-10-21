#!/bin/bash

until /usr/local/bin/mongo -u debezium -p dbz "$MONGODB_HOST/admin" --eval "print(\"Waited for debezium user to be created\")"; do
  echo "Waiting for debezium user to be created in MongoDB"
  sleep 2
done

echo "User debezium created in MongoDB"
echo "Starting Kogito"

bash -c '/deployments/run-java.sh'
