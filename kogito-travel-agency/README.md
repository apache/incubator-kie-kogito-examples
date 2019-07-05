# Kogito Travel Agency - base version

## Description

A travel agency service that allows to plan travels for travellers. Very basic versions shows how to
make use of Kogito with Quarkus to develop highly efficient services. 

## Installing and Running

### Prerequisites
 
You will need:
  - Java 1.8.0+ installed 
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.5.4+ installed

When using native image compilation, you will also need: 
  - GraalVM 1.0.0-rc16 installed - note that GraalVM 19.0+ does not (yet) work with Quarkus for native image compilation, this should be updated soon but please use 1.0.0.rc16 until then
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

### Compile and Run in Local Dev Mode

```
mvn clean package quarkus:dev    
```

### Compile and Run using Local Native Image
Note that this requires GRAALVM_HOME to point to a valid GraalVM installation

```
mvn clean package -Pnative
```
  
To run the generated native executable, generated in `target/`, execute

```
./target/kogito-travel-agency-{version}-runner
```

## Example Usage

Once the service is up and running, you can use the following examples to interact with the service.

### POST /travels

Send travel that requires does not require visa

```sh
curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST http://localhost:8080/travels -d @- << EOF 
{ 
	"traveller" : { 
		"firstName" : "John", 
		"lastName" : "Doe", 
		"email" : "john.doe@example.com", 
		"nationality" : "American", 
		"address" : { 
			"street" : "main street", 
			"city" : "Boston", 
			"zipCode" : "10005", 
			"country" : "US" 
		} 
	}, 
	"trip" : { 
		"city" : "New York", 
		"country" : "US", 
		"begin" : "2019-12-10T00:00:00.000+02:00", 
		"end" : "2019-12-15T00:00:00.000+02:00" 
	} 
}
EOF
    
```

This will directly go to confirmation user task.

Send travel request that requires does require visa

```sh
curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST http://localhost:8080/travels -d @- << EOF 
{ 
	"traveller" : { 
		"firstName" : "Jan", 
		"lastName" : "Kowalski", 
		"email" : "jan.kowalski@example.com", 
		"nationality" : "Polish", 
		"address" : { 
			"street" : "polna",
			"city" : "Krakow", 
			"zipCode" : "32000", 
			"country" : "Poland" 
		} 
	}, 
	"trip" : { 
		"city" : "New York", 
		"country" : "US", 
		"begin" : "2019-12-10T00:00:00.000+02:00", 
		"end" : "2019-12-15T00:00:00.000+02:00" 
	} 
}
EOF
```

This will stop at 'VisaApplication' user task.

### GET /travels

Returns list of travel requests currently active:

```sh
curl -X GET http://localhost:8080/travels
```

As response an array of travels is returned.

### GET /travels/{id}

Returns travel request with given id (if active):

```sh
curl -X GET http://localhost:8080/travels/1
```

As response a single travel request is returned if found, otherwise no content (204) is returned.

### DELETE /travels/{id}

Cancels travel request with given id

```sh
curl -X DELETE http://localhost:8080/travels/1
```