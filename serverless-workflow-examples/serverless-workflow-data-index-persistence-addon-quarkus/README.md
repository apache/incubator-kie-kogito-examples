# Kogito Serverless Workflow - Data Index persistence addon Example

## Description

This example contains a simple workflow service that demonstrates how to use Data Index persistence addon as part of the Kogito runtime. 
The service is described using JSON format as defined in the 
[CNCF Serverless Workflow specification](https://github.com/serverlessworkflow/specification).

## Infrastructure requirements

### PostgreSQL

This example also requires persistence with a PostgreSQL server.

Optionally and for convenience, a docker-compose [configuration file](docker-compose/docker-compose.yml) is
provided in the path [docker-compose/](docker-compose/), where you can just run the command from there:

```sh
./startServices.sh
```

The configuration for setting up the connection can be found in [applications.properties](src/main/resources/application.properties) file, which
follows the Quarkus JDBC settings, for more information please check [JDBC Configuration Reference](https://quarkus.io/guides/datasource#jdbc-configuration).

In this way a container for PostgreSQL will be started on port 5432.

## Installing and Running

### Prerequisites
 
You will need:
  - Java 17+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.9.6+ installed
  - Docker and Docker Compose to run the required example infrastructure.

When using native image compilation, you will also need: 
    - GraalVM 22.2+ installed
    - Environment variable GRAALVM_HOME set accordingly
    - GraalVM native image needs as well native-image extension: https://www.graalvm.org/reference-manual/native-image/
    - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

NOTE: Quarkus provides a way of creating a native Linux executable without GraalVM installed, leveraging a container runtime such as Docker or Podman. More details in  https://quarkus.io/guides/building-native-image#container-runtime 

### Compile and Run in Local Dev Mode

```sh
mvn clean package quarkus:dev
```

NOTE: Data Index graphql UI will be available in http://localhost:8180/graphiql/


### Start infrastructure services

You should start all the services before you execute any of the **Data Index** example. To do that please execute:

```sh
mvn clean package -Pcontainer
```

For Linux and MacOS:

1. Open a Terminal
2. Go to docker-compose folder
3. Run ```docker-compose up```

```bash
cd docker-compose && ./startServices.sh
```

TIP: If you get a `permission denied` error while creating the postgresql container, consider using SELinux context.
Update the following line:
```yaml
    - ./sql:/docker-entrypoint-initdb.d
```
to
```yaml
    - ./sql:/docker-entrypoint-initdb.d:Z
```

Once all services bootstrap, the following ports will be assigned on your local machine:

- PostgreSQL: 5432
- PgAdmin: 8055
- Data Index service: 8180
- serverless-workflow-service: 8080

> **_NOTE:_**  This step requires the project to be compiled, please consider running a ```mvn clean package -Dcontainer``` command on the project root before running the ```docker-compose up``` for the first time or any time you modify the project.

Once started you can simply stop all services by executing the ```docker-compose stop```.

All created containers can be removed by executing the ```docker-compose rm```.

### Submit a request

The service based on the JSON workflow definition can be access by sending a request to http://localhost:8080/greet'
with following content

```json
{
  "name": "John",
  "language": "English"
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"name": "John", "language": "English"}' http://localhost:8080/greet
```

Log after curl executed:

```json
{"id":"541a5363-1667-4f6d-a8b4-1299eba81eac","workflowdata":{"name":"John","language":"English","greeting":"Hello from JSON Workflow, "}}
```

In Quarkus you should see the log message printed:

```text
Hello from JSON Workflow, John
```

If you would like to greet the person in Spanish, we need to pass the following data on workflow start:

```json
{
  "name": "John",
  "language": "Spanish"
}
```

Complete curl command can be found below:

```sh
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"name": "John", "language": "Spanish"}' http://localhost:8080/greet
```

In Quarkus you should now see the log message printed:

```text
Saludos desde JSON Workflow, John
```

Then we can verify that the data has been properly indexed accessing to http://localhost:8180/graphiql/ and executing the query:

```text
{ProcessInstances {
  id 
  variables
}}
```

getting as a result:

```text
{
  "data": {
    "ProcessInstances": [
      {
        "id": "0b95e8a1-b52f-48cf-b7d0-38fa3087d467",
        "variables": {
          "workflowdata": {
            "name": "John",
            "greeting": "Hello from JSON Workflow, ",
            "language": "English"
          }
        }
      },
      {
        "id": "141f7350-7802-4abc-985c-333caf1068f9",
        "variables": {
          "workflowdata": {
            "name": "John",
            "greeting": "Saludos desde JSON Workflow, ",
            "language": "Spanish"
          }
        }
      }
    ]
  }
}
```

Or by command line, executing the complete curl command can be found below:

```sh
curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST --data '{"query" : "{ProcessInstances {id variables}}" }' http://localhost:8180/graphql
```

getting

```text
{"data":{"ProcessInstances":[{"id":"0b95e8a1-b52f-48cf-b7d0-38fa3087d467","variables":{"workflowdata":{"name":"John","greeting":"Hello from JSON Workflow, ","language":"English"}}},{"id":"141f7350-7802-4abc-985c-333caf1068f9","variables":{"workflowdata":{"name":"John","greeting":"Saludos desde JSON Workflow, ","language":"Spanish"}}}]}}
```