### Start test Kafka instance via Docker Compose

There's a useful [docker-compose.yml](docker-compose.yml) in the root that starts a dedicated Kafka instance for quick tests.

Simply start it with this command from the root of the repo:

```
docker-compose up -d
```

### Package and Run in JVM mode

```
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```


## Test with Kafka

Send Event with JSON format to "events" topic.

```sh
echo '{"type":"temperature","value":35}' | kafka-console-producer.sh --broker-list localhost:9092 --topic events
```

You will see the result in "alerts" topic via Kafdrop (http://localhost:9000).

```json
{"severity":"warning","message":"Event [type=temperature, value=35]"}
```


## Test with REST

### POST /warnings

Returns warning level alerts caused by incoming events:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"eventData":[{"type":"temperature","value":25}, {"type":"temperature","value":35}]}' http://localhost:8080/warnings
```

Example response:

```json
[{"severity":"warning","message":"Event [type=temperature, value=40]"}]
```
