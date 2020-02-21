# Drools + Quarkus config example

## Description

PoC for kogito to export grafana dashboard during codegen and export live metrics to prometheus.

## Installing and Running

Run the following commands that will download the kogito patch containing the modified codegen and install it. It will also download my jgrafana library to generate dashboards. 

```sh
cd docker-compose
./setup.sh
```

### Prerequisites
 
You will need:
  - Java 1.8.0+ installed 
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.5.4+ installed

### Compile and Run in Local Dev Mode

Run
```sh
cd  docker-compose
./run-compose.sh
```
It will compile the app and spin it up together with a grafana and prometheus instances.

## Example Usage

Once the service is up and running, you can use the following example to interact with the service.

### POST /hello

Post "hello":

```sh

```

the service will return `["hello", "world"]`
