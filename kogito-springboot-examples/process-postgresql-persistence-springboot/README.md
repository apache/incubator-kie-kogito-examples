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

This quickstart requires a PostgreSQL server to be available with a database, a user and credentials already created
, these configurations should then be set in the connection URI parameter in [applications.properties](src/main/resources/application.properties) file with the key
 `kogito.persistence.postgresql.connection.uri`, i.e `postgresql.connection.uri=postgresql://kogito-user:kogito-pass@localhost:5432/kogito` here are the [full settings for URI](https://www.postgresql.org/docs/9.6/static/libpq-connect.html#LIBPQ-CONNSTRING)    
 
You must set the property `kogito.persistence.type=postgresql` to enable PostgreSQL persistence. There is also a
configuration to allow the application to run DDL scripts during the initialization, which you can enable with the
property `kogito.persistence.auto.ddl=true`.
For more details you can check [applications.properties](src/main/resources/application.properties).

Optionally and for convenience, a docker-compose [configuration file](docker-compose/docker-compose.yml) is
 provided in the path [docker-compose/](docker-compose/), where you can just run the command from there:
  ```sh
  docker-compose up
  ```  
  In this way a container for PostgreSQL running on port 5432, along with PgAdmin, running on port
   8055 to allow the database management.
  
  The default admin user for PostgreSQL is `postgres` with password `pass`, for PgAdmin the default user created is
   `user@user.org` with password `pass`, the database connection could be set in PgAdmin using the hostname 
   `postgres-container` for the PostgreSQL server, details defined in [configuration file](docker-compose/docker-compose.yml),  an initializer script is executed to create the `kogito` database and `kogito-user`.

## Build and run

### Prerequisites
 
You will need:
  - Java 11+ installed 
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.6+ installed

### Compile and Run in Local Dev Mode

```
mvn clean package spring-boot:run
```

NOTE: With dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules, decision tables and java code. No need to redeploy or restart your running application.

Once PostgreSQL is up and running you can build this project with -Ppersistence OR -Pjdbc-persistence in an exact same way as without persistence. These extra profile in maven configuration add additional dependencies needed to work with Postgres as persistent store using Reactive or JDBC based postgres clients.

Kogito runtimes need to be able to safely handle concurrent requests to shared instances such as process instances, tasks, etc. This feature is optional and can be pluggable with persistence using the following property and value to the src/main/resources/application.properties file.

```
kogito.persistence.optimistic.lock=true 
```
### Compile and Run using uberjar

```
mvn clean package
```
  
To run the generated native executable, generated in `target/`, execute

```
java -jar target/process-postgresql-persistence-springboot.jar
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

```bash
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"name" : "my fancy deal", "traveller" : { "firstName" : "John", "lastName" : "Doe", "email" : "jon.doe@example.com", "nationality" : "American","address" : { "street" : "main street", "city" : "Boston", "zipCode" : "10005", "country" : "US" }}}' http://localhost:8080/deals
```

this will then trigger the review user task that you can work with.

### Get review task for given deal

You can display all active reviews of deals by:

```bash
curl http://localhost:8080/usertasks/instance?user=john
```

### Complete review task for given deal

Last but not least, you can complete the review user task by:

```bash
curl -X POST "http://localhost:8080/usertasks/instance/{taskId}/transition?user=john" -H "content-type: application/json" -d '{"transitionId": "complete","data": {"review" : "very good work", "approve": true}}'
```

Where `{taskId}` is the id of the user task you want to complete.

The review Log should look similar to:

```
Review of the deal very good work for traveller Doe
```
