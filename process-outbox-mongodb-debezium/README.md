# Description

An example to solve the Kogito event consistency issue with Transactional Outbox based on MongoDB and Debezium.  

# References

**Outbox pattern**: https://debezium.io/blog/2019/02/19/reliable-microservices-data-exchange-with-the-outbox-pattern/

**debezium-examples:** https://github.com/debezium/debezium-examples/blob/master/tutorial/README.md#using-mongodb

**debezium-images:** https://github.com/debezium/docker-images/tree/master/examples/mongodb/

# Run the Examples

1. Set Debezium version
```shell
export DEBEZIUM_VERSION=1.7
```

2. Build the customized MongoDB image
```shell
docker build -f debezium/docker-mongo/Dockerfile -t example-mongodb-4.4:${DEBEZIUM_VERSION} debezium/docker-mongo
```

3. Deploy MongoDB, Debezium and Kafka
```shell
docker-compose -f debezium/docker-compose-mongodb.yaml up
```

4. Initialize MongoDB replica set and insert some test data
```shell
docker-compose -f debezium/docker-compose-mongodb.yaml exec mongodb bash -c '/usr/local/bin/init-kogito.sh'
```

5. Start MongoDB connector
```shell
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @debezium/register-mongodb.json
```

6. Access the database via MongoDB client if needed
```shell
docker-compose -f debezium/docker-compose-mongodb.yaml exec mongodb bash -c 'mongo -u $MONGODB_USER -p $MONGODB_PASSWORD --authenticationDatabase admin kogito'
```

7. Navigate to the [process-mongodb-transaction-quarkus](../process-mongodb-transaction-quarkus) or [process-mongodb-transaction-springboot](../process-mongodb-transaction-springboot) example directory, build and run the Kogito App, create an example process instance, and interact with the service to generate some Kogito events

8. Consume messages from an event topic
```shell
docker-compose -f debezium/docker-compose-mongodb.yaml exec kafka /kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server kafka:9092 \
    --from-beginning \
    --property print.key=false \
    --topic kogito-processinstances-events
```

9. With the Kafka broker info from step 8, run the Kogito Data Index Service with MongoDB to consume Kafka messages: https://github.com/kiegroup/kogito-runtimes/wiki/Data-Index-Service

10. Shut down the cluster
```shell
docker-compose -f debezium/docker-compose-mongodb.yaml down
```