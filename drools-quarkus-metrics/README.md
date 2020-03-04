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
  - Linux SO (No Windows atm)

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
./sample-requests/query-drl-hello.sh

```

the service will return `["hello", "world"]`

### POST /LoanEligibility

Post:

```sh
./sample-requests/query-dmn-loan.sh

```

the service will return the decision results. You can generate some traffic with 

```sh
while true; do ./sample-requests/query-dmn-loan.sh; done
```

for example. 

Go to `localhost:3000` and have a look at your dashboards. Here a screenshot of what you will get

![Screenshot from 2020-02-21 14-54-20](https://user-images.githubusercontent.com/18282531/75046661-cd6d3c00-54c5-11ea-81a7-2afcd09aea7b.png)



FOR SPRINGBOOT: 

Add to `application.resources` 
```
#server.address=0.0.0.0
#spring.mvc.servlet.path=/docs
management.endpoints.web.base-path=/chupa
resteasy.jaxrs.scan-packages=org.kie.kogito.**,http*
management.endpoints.web.exposure.include=*
management.server.port=8081
logging.level.root=INFO
management.metrics.web.server.auto-time-requests=false
management.metrics.distribution.percentiles-histogram.http.server.requests=true
management.metrics.distribution.sla.http.server.requests=1ms,5ms
```

Add main class like 
```

@SpringBootApplication(scanBasePackages={"org.kie.dmn.kogito.**", "org.kie.kogito.app.**", "org.kie.kogito.**"})
public class MicrometerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicrometerApplication.class, args);
    }
}
```

Add to pom.xml
```
    <!-- https://mvnrepository.com/artifact/javax.inject/javax.inject -->
    <dependency>
      <groupId>javax.inject</groupId>
      <artifactId>javax.inject</artifactId>
      <version>1</version>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>


    <!-- https://mvnrepository.com/artifact/io.micrometer/micrometer-registry-prometheus -->
    <dependency>
      <groupId>io.micrometer</groupId>
      <artifactId>micrometer-registry-prometheus</artifactId>
      <version>1.3.5</version>
    </dependency>
```
