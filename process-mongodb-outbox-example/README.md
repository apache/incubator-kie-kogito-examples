# Description

An example to solve the Kogito event consistency issue with Transactional Outbox based on MongoDB and Debezium.  

# References

**Outbox pattern**: https://debezium.io/blog/2019/02/19/reliable-microservices-data-exchange-with-the-outbox-pattern/

**debezium-examples:** https://github.com/debezium/debezium-examples/blob/master/tutorial/README.md#using-mongodb

**debezium-images:** https://github.com/debezium/docker-images/tree/master/examples/mongodb/

# Setup Environment

```shell
# Set Debezium version
export DEBEZIUM_VERSION=1.4

# Build the customized MongoDB image
docker build -f debezium/docker-mongo/Dockerfile -t example-mongodb-4.4:${DEBEZIUM_VERSION} debezium/docker-mongo

# Deploy MongoDB, Debezium and Kafka
docker-compose -f debezium/docker-compose-mongodb.yaml up

# Initialize MongoDB replica set and insert some test data
docker-compose -f debezium/docker-compose-mongodb.yaml exec mongodb bash -c '/usr/local/bin/init-inventory.sh'

# Start MongoDB connector
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @debezium/register-mongodb.json

# Access the database via MongoDB client if needed
docker-compose -f debezium/docker-compose-mongodb.yaml exec mongodb bash -c 'mongo -u $MONGODB_USER -p $MONGODB_PASSWORD --authenticationDatabase admin inventory'

# Navigate to the quarkus or springboot example directory, build and run the Kogito App, create an example process instance

# Consume messages from an event topic
docker-compose -f debezium/docker-compose-mongodb.yaml exec kafka /kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server kafka:9092 \
    --from-beginning \
    --property print.key=false \
    --topic kogito-processinstances-events

# Shut down the cluster
docker-compose -f debezium/docker-compose-mongodb.yaml down
```