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
  - [jq](https://stedolan.github.io/jq) tool installed. You can download it from [here](https://stedolan.github.io/jq/download)
  - Docker to be able to install keycloak server

#### Starting and Configuring the Keycloak Server

To start a Keycloak Server you can use Docker and just run the following command:

```sh
docker run -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin  -e KEYCLOAK_IMPORT=/tmp/kogito-realm.json -v <kogito-quickstarts_absolute_path>/kogito-usertasks-with-security-oidc-springboot/config/kogito-realm.json:/tmp/kogito-realm.json -p 8281:8080  quay.io/keycloak/keycloak:legacy
```

You should be able to access your Keycloak Server at [localhost:8281/auth](http://localhost:8281/auth).
and verify keycloak server is running properly: log in as the admin user to access the Keycloak Administration Console.
Username should be admin and password admin.
With the keycloak kogito realm  imported we have defined users to be able to try the different endpoints
user:
    john with role 'employees'
    mary with role 'managers'
    poul with roles 'interns and managers'

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
java -jar target/process-usertasks-with-security-oidc-springboot.jar
```

### OpenAPI (Swagger) documentation
[Specification at swagger.io](https://swagger.io/docs/specification/about/)

You can take a look at the [OpenAPI definition](http://localhost:8080/v3/api-docs) - automatically generated and included in this service - to determine all available operations exposed by this service. For easy readability you can visualize the OpenAPI definition file using a UI tool like for example available [Swagger UI](https://editor.swagger.io).

In addition, various clients to interact with this service can be easily generated using this OpenAPI definition.

### Submit a request to start new approval
The application is using bearer token authorization and the first thing to do is obtain an access token from the Keycloak
Server in order to access the application resources. Obtain an access token for user john.

```sh
export access_token=$(\
    curl -X POST http://localhost:8281/auth/realms/kogito/protocol/openid-connect/token \
    --user kogito-app:secret \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=john&password=john&grant_type=password' | jq --raw-output '.access_token' \
)
```

To make use of this application it is as simple as putting a sending request to `http://localhost:8080/approvals`

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

Complete curl command can be found below, passing the token as Authorization header :

```sh
curl -X POST -H "Authorization: Bearer "$access_token -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"traveller" : { "firstName" : "John", "lastName" : "Doe", "email" : "jon.doe@example.com", "nationality" : "American","address" : { "street" : "main street", "city" : "Boston", "zipCode" : "10005", "country" : "US" }}}' http://localhost:8080/approvals
```

### Show active approvals

```sh
curl -H 'Content-Type:application/json' -H 'Accept:application/json' -H "Authorization: Bearer "$access_token http://localhost:8080/approvals
```

### Show tasks

```sh
curl -H 'Content-Type:application/json' -H 'Accept:application/json' -H "Authorization: Bearer "$access_token 'http://localhost:8080/approvals/{uuid}/tasks?user=john&group=employees'
```

Try with the manager Mary

```sh
export access_token=$(\
    curl -X POST http://localhost:8281/auth/realms/kogito/protocol/openid-connect/token \
    --user kogito-app:secret \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=mary&password=mary&grant_type=password' | jq --raw-output '.access_token' \
 )
```

```sh
curl -H 'Content-Type:application/json' -H 'Accept:application/json' -H "Authorization: Bearer "$access_token 'http://localhost:8080/approvals/{uuid}/tasks?user=mary&group=managers'
```

where `{uuid}` is the id of the given approval instance


### Complete first line approval task

```sh
curl -H "Authorization: Bearer "$access_token -X POST -d '{"approved" : true}' -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/firstLineApproval/{tuuid}?user=mary&group=managers'
```

where `{uuid}` is the id of the given approval instance and `{tuuid}` is the id of the task

### Show tasks

```sh
curl -H "Authorization: Bearer "$access_token -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/tasks?user=mary&group=managers'
```

where `{uuid}` is the id of the given approval instance.
This should return empty response as Mary was the first approver and another user with role managers needs to approve the Second
Line.

Repeating the request with another user with role managers

```sh
export access_token=$(\
    curl -X POST http://localhost:8281/auth/realms/kogito/protocol/openid-connect/token \
    --user kogito-app:secret \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=poul&password=poul&grant_type=password' | jq --raw-output '.access_token' \
 )
```

```sh
curl -H "Authorization: Bearer "$access_token -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/tasks?user=poul&group=managers'
```

Now we have the id for the second approval task

### Complete second line approval task

```sh
curl -H "Authorization: Bearer "$access_token -X POST -d '{"approved" : true}' -H 'Content-Type:application/json' -H 'Accept:application/json' 'http://localhost:8080/approvals/{uuid}/secondLineApproval/{tuuid}?user=poul&group=managers'
```

where `{uuid}` is the id of the given approval instance and `{tuuid}` is the id of the task instance

This completes the approval and returns approvals model where both approvals of first and second line can be found,
plus the approver who made the first one.

```json
{
 "id":"{uuid}",
 "approver":"mary",
 "firstLineApproval":true,
 "secondLineApproval":true,
 "traveller":{
       "firstName":"John",
       "lastName":"Doe",
       "email":"jon.doe@example.com",
       "nationality":"American",
       "address":{
           "street":"main street",
           "city":"Boston",
           "zipCode":"10005",
           "country":"US"
       }
  }
}
```
