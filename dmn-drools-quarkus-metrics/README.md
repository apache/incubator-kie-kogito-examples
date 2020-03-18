# Drools + Quarkus + Runtime metrics

## Description

This example demonstrates how to enable and consume the runtime metrics monitoring feature in Kogito. 

### Prerequisites
 
You will need:
  - Java 1.8.0+ installed 
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.5.4+ installed
  - Linux SO (No Windows atm?)

## Installing

[As of 16/03/2020] The PR is not merged yet, so you have to install the feature from the branch: run the following commands that will download the kogito patch containing the modified codegen and install it. 

```sh
cd docker-compose
./setup.sh
```

### How to enable the feature

Import the following dependency in your `pom.xml`:

```XML
<dependency>
 <groupId>org.kie.kogito</groupId>
 <artifactId>monitoring-prometheus-addon</artifactId>
</dependency>
```

And add the following class in your quarkus project: 

```Java
package org.kie.kogito.examples;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.ext.Provider;

import org.kie.addons.monitoring.system.interceptor.MetricsInterceptor;

@Provider
public class MyInterceptor extends MetricsInterceptor {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        super.filter(requestContext, responseContext);
    }
}
```

### Architecture

Once you compile your Quarkus project, a dashboard for each available endpoint will be generated under the path `target/generated-sources/kogito/dashboards/`. You can then inject those grafana dashboards during the deployment of the grafana instance and that's it.

The use case is summarized in the following schema: 
![RuntimeMetrics](https://user-images.githubusercontent.com/18282531/76740726-a0cbdd80-676e-11ea-8cc3-63ed5cbb3ac8.png)

To summarize, the kogito app will expose by default an endpoint `/metrics` with the prometheus variables, and a prometheus instance will simply fetch the data from there.

### Dashboards

Two kind of dashboards will be generated depending on the model used on the endpoint: 
1. DRL: A basic dashboard is exported and it contains: 
a. Total number of requests on the endpoint.
b. Average per minute of the number of requests on the endpoint.
c. Quantiles on the elapsed time to evaluate the requests.
d. Exception details.
![Screenshot from 2020-03-16 11-08-21](https://user-images.githubusercontent.com/18282531/76745628-79790e80-6776-11ea-87ca-6f56233c38a8.png)
2. DMN: A dashboard *containing all the "standard" information already described above for the DRL dashboard* plus a graph for each type of decision in the DMN model depending on the type of the decision:
a. if the output of the decision is a number, the quantiles for that metric (on a sliding window of 3 minutes).
b. If the output is a boolean or a string, the number of occurrences for each output (10 minutes average).
![Screenshot from 2020-03-16 10-58-45](https://user-images.githubusercontent.com/18282531/76744997-674aa080-6775-11ea-801e-5ef0484206ad.png)


You can use this default dashboards, or you can personalize them and use your custom dashboards.

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

Go to `localhost:3000` and have a look at your dashboards.


FOR SPRINGBOOT (TBD): 

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
