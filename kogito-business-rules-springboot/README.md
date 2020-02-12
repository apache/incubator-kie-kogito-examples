# Kogito with business rules

## Description

A quickstart project that shows the use of business rules and processes

This example shows

* make use of DRL to define rules
* make use of business rules task in the process to evaluate rules
	
	
<p align="center"><img width=75% height=50% src="docs/images/process.png"></p>


* DRL File
<p align="center"><img src="docs/images/drl.png"></p>

* Diagram Properties
<p align="center"><img src="docs/images/diagramProperties.png"></p>

* Diagram Properties
<p align="center"><img src="docs/images/diagramProperties2.png"></p>

* Diagram Properties
<p align="center"><img src="docs/images/diagramPropertiesDataImports.png"></p>

* Evaluate Person Business Rule
<p align="center"><img src="docs/images/evaluatePersonBusinessRule.png"></p>

* Evaluate Person Business Rule
<p align="center"><img src="docs/images/evaluatePersonBusinessRule2.png"></p>

* Evaluate Person Business Rule
<p align="center"><img src="docs/images/evaluatePersonBusinessRuleDataAssignments.png"></p>

* Special Handling for Children
<p align="center"><img src="docs/images/specialHandlingForChildren.png"></p>

* Special Handling for Children
<p align="center"><img src="docs/images/specialHandlingForChildren2.png"></p>

* Special Handling for Children
<p align="center"><img src="docs/images/specialHandlingForChildren3.png"></p>

* Special Handling for Children
<p align="center"><img src="docs/images/specialHandlingForChildrenAssignments.png"></p>


## Build and run

### Prerequisites
 
You will need:
  - Java 1.8.0+ installed 
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.5.4+ installed

### Compile and Run in Local Dev Mode

```
mvn clean package spring-boot:run    
```


### Compile and Run using uberjar

```
mvn clean package 
```
  
To run the generated native executable, generated in `target/`, execute

```
java -jar target/kogito-business-rules-sprintboot-{version}.jar
```

### Use the application


### Submit a request

To make use of this application it is as simple as putting a sending request to `http://localhost:8080/persons`  with following content 

```
{
  "person" : {
    "name" "john",
    "age" : 20
  }
}

```

Complete curl command can be found below for adults:

```
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"person" : {"name" : "john", "age" : 20}}' http://localhost:8080/persons
```

After the Curl command you should see a similar console log

<p align="center"><img src="docs/images/consoleLog.png"></p>


Complete curl command can be found below for children:

```
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"person" : {"name" : "john", "age" : 5}}' http://localhost:8080/persons
```

After the Curl command you should see a similar console log

<p align="center"><img src="docs/images/consoleLog2.png"></p>


