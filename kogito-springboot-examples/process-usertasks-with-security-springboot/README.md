# Process user task orchestration

## Description

A quickstart project shows very typical user task orchestration. It comes with two tasks assigned
to human actors via groups assignments - `managers`. So essentially anyone who is a member of that
group can act on the tasks. Though this example applies four eye principle which essentially means
that user who approved first task cannot approve second one. So there must be always at least two
distinct manager involved.

This example shows

* working with user tasks
* four eye principle with user tasks


<p align="center"><img width=75% height=50% src="docs/images/process.png"></p>


## Build and run

### Prerequisites

You will need:
  - Java 11+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.8.6+ installed

### Compile and Run in Local Dev Mode

```sh
mvn clean compile spring-boot:run
```


### Package and Run using uberjar

```sh
mvn clean package
```

To run the generated native executable, generated in `target/`, execute

```sh
java -jar target/process-usertasks-with-security-springboot.jar
```

### OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/v3/api-docs) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.


### Submit a request to start new approval

To make use of this application it is as simple as putting a sending request to `http://localhost:8080/approvals`  with following content

```json
{
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

```sh
curl -u john:john -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"traveller" : { "firstName" : "John", "lastName" : "Doe", "email" : "jon.doe@example.com", "nationality" : "American","address" : { "street" : "main street", "city" : "Boston", "zipCode" : "10005", "country" : "US" }}}' http://localhost:8080/approvals
```

### Show active approvals

```sh
curl -u john:john -H 'Content-Type:application/json' -H 'Accept:application/json' http://localhost:8080/approvals
```

### Show tasks

```sh
curl -u john:john -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/tasks?user=john&group=managers'
```

where `{uuid}` is the id of the given approval instance


### Complete first line approval task

```sh
curl -u john:john -X POST -d '{"approved" : true}' -H 'Content-Type:application/json' -H 'Accept:application/json' http://localhost:8080/approvals/{uuid}/Task/{tuuid}?user=john&group=managers'
```

where `{uuid}` is the id of the given approval instance and `{tuuid}` is the id of the task instance

### Show tasks

```sh
curl -u john:john -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/tasks?user=john&group=managers'
```

where `{uuid}` is the id of the given approval instance

This should return empty response as the admin user was the first approver and by that can't be assigned to another one.

Repeating the request with another user will return task

```sh
curl -u mary:mary -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/tasks?user=mary&group=managers'
```


### Complete second line approval task

```sh
curl -u mary:mary -X POST -d '{"approved" : true}' -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/Task/{tuuid}?user=mary&group=managers'
```

where `{uuid}` is the id of the given approval instance and `{tuuid}` is the id of the task instance

This completes the approval and returns approvals model where both approvals of first and second line can be found,
plus the approver who made the first one.

```json
{
	"approver":"admin",
	"firstLineApproval":true,
	"id":"2eeafa82-d631-4554-8d8e-46614cbe3bdf",
	"secondLineApproval":true,
	"traveller":{
		"address":{
			"city":"Boston",
			"country":"US",
			"street":"main street",
			"zipCode":"10005"},
		"email":"jon.doe@example.com",
		"firstName":"John",
		"lastName":"Doe",
		"nationality":"American"
	}
}
```
