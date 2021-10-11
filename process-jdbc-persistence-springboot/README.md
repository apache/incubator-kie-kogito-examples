# Process with persistence powered by PostgreSQL

## Description

A quickstart project that processes deals for travellers. It utilizes process composition to split the work of

* submitting a deal
* reviewing a deal

At the same time shows a simplified version of an approval process that waits for a human actor to provide a review.

This example shows:

* exposing Submit Deal as public service
* each process instance is going to be evaluated and asks for review
* at any point in time, the service can be shutdown, and when brought back, it will keep the state of the instances

Note: The use of this example shows that the data sent to PostgreSQL is saved. You can shut down the application and restart it, and as long as PostgreSQL is running after you restart you should still see the data.

It utilizes PostgreSQL server as the backend store.

* Process (submitDeal.bpmn)
<p align="center"><img width=75% height=50% src="docs/images/process.png"></p>

* Process Properties (top)
<p align="center"><img src="docs/images/processProperties.png"></p>

* Process Properties (bottom)
<p align="center"><img src="docs/images/processProperties2.png"></p>

* Call a deal
<p align="center"><img src="docs/images/callADeal.png"></p>

* Call a deal (Assignments)
<p align="center"><img src="docs/images/callADeal2.png"></p>

* Print review the Deal
<p align="center"><img src="docs/images/printReviewTheDeal.png"></p>

* Subprocess (reviewDeal.bpmn)
<p align="center"><img width=75% height=50% src="docs/images/subprocess.png"></p>

* Deal Review (top)
<p align="center"><img src="docs/images/dealReview.png"></p>

* Deal Review (bottom)
<p align="center"><img src="docs/images/dealReview3.png"></p>

* Review deal user task	(top)
<p align="center"><img src="docs/images/reviewDealUserTask.png"></p>

* Review deal user task	(botom)
<p align="center"><img src="docs/images/reviewDealUserTask2.png"></p>

* Review deal user task	(Assignments)
<p align="center"><img src="docs/images/reviewDealUserTask3.png"></p>

## Infrastructure requirements

This quickstart requires a PostgreSQL/Oracle server to be available with a database. Example configuration can be found in [postgresql.properties](src/main/resources/postgresql.properties) and [oracle.properties](src/main/resources/oracle.properties).

You must set the property `kogito.persistence.type=jdbc` to enable JDBC persistence. There is also a configuration to allow the application to run DDL scripts during the initialization, which you can enable with the property `kogito.persistence.auto.ddl=true`.
For more details you can check [applications.properties](src/main/resources/application.properties).

Optionally and for convenience, a docker-compose setup is provided.

### Postgres
Postgres [configuration file](docker-compose/postgres-compose.yml) is provided in the path [postgres-compose/](postgres-compose/), where you can just run the command from there:
  ```sh
  docker-compose -f postgres-compose.yaml up
  ```
  In this way a container for PostgreSQL running on port 5432.

  The default admin user for PostgreSQL is `postgres` with password `pass`.

### Oracle
Oracle [configuration file](docker-compose/oracle-compose.yml) is provided in the path [oracle-compose/](oracle-compose/), where you can just run the command from there:
  ```sh
  docker-compose -f oracle-compose.yaml up
  ```
  In this way a container for Oracle running on port 1521.

  The default admin user for Oracle is `system` with password `oracle`.

## Build and run

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed

### Compile and Run

To start up a the application specify the profile for the particular database (jdbc-postgres/jdbc-oracle). eg:
```
mvn clean package spring-boot:run -Pjdbc-postgres
```

Once the database is up and running you can build this project with using the same profile. These extra profile in maven configuration add additional dependencies needed to work with the database as persistent store using JDBC based clients.

Kogito runtimes need to be able to safely handle concurrent requests to shared instances such as process instances, tasks, etc. This feature is optional and can be pluggable with persistence using the following property and value to the src/main/resources/application.properties file.

```
kogito.persistence.optimistic.lock=true
```

### Compile and Run using uberjar
Use the same profile(jdbc-postgres/jdbc-oracle) as previous to build an uberjar . eg:
```
mvn clean package -Pjdbc-postgres
```

To run the generated native executable, generated in `target/`, execute

```
java -jar target/process-jdbc-persistence-springboot.jar
```

### OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/v3/api-docs) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.


### Submit a deal

To make use of this application it is as simple as putting a sending request to `http://localhost:8080/deals`  with following content

```
{
"name" : "my fancy deal",
"traveller" : {
  "firstName" : "John",
  "lastName" : "Doe",
  "email" : "jon.doe@example.com",
  "nationality" : "American",
  "address" : {
  	"street" : "main street",
  	"city" : "Boston",
  	"zipCode" : "10005",
  	"country" : "US" }
  }
}

```

Complete curl command can be found below:

```
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"name" : "my fancy deal", "traveller" : { "firstName" : "John", "lastName" : "Doe", "email" : "jon.doe@example.com", "nationality" : "American","address" : { "street" : "main street", "city" : "Boston", "zipCode" : "10005", "country" : "US" }}}' http://localhost:8080/deals
```

this will then trigger the review user task that you can work with.

### Get review task for given deal

First you can display all active reviews of deals

```
curl -H 'Content-Type:application/json' -H 'Accept:application/json' http://localhost:8080/dealreviews
```

based on the response you can select one of the reviews to see more details

```
curl -H 'Content-Type:application/json' -H 'Accept:application/json' http://localhost:8080/dealreviews/{uuid}/tasks?user=john
```

where uuid is the id of the deal review you want to work with.

Next you can get the details assigned to review user task by

```
curl -H 'Content-Type:application/json' -H 'Accept:application/json' http://localhost:8080/dealreviews/{uuid}/review/{tuuid}?user=john
```

where uuid is the id of the deal review and tuuid is the id of the user task you want to get


### Complete review task for given deal

Last but not least you can complete review user task by

```
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"review" : "very good work"}' http://localhost:8080/dealreviews/uuid/review/{tuuid}?user=john
```

where uuid is the id of the deal review and tuuid is the id of the user task you want to get

* Review Log should look similar to

```
Review of the deal very good work for traveller Doe
```
