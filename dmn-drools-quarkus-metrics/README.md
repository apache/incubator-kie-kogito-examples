# Drools + Quarkus + Runtime metrics

## Description

This example demonstrates how to enable and consume the runtime metrics monitoring feature in Kogito. 

### Prerequisites
 
You will need:
  - Java 11+ installed 
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.6.2+ installed
  
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

Once you compile your Quarkus project, a dashboard for each available endpoint will be generated under the path `target/resources/dashboards/`. You can then inject those grafana dashboards during the deployment of the grafana instance.

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

A script `docker-compose/run-compose.sh` is provided to demonstrate how to inject the generated dashboards in the volume of the grafana container:
 1. the generated dashboards are copied from `target/resources/dashboards/` to the directory `docker-compose/grafana/provisioning/dashboards` 
 2. The volumes of the grafana container are properly set in the `docker-compose.yml` file, so that the dashboards are properly loaded at startup.
 3. `docker-compose` is run. 

## Example Usage

Once the service is up and running, you can use the following example to interact with the service.

### POST /hello

Post "hello":

```sh
curl -H "Content-Type: application/json" -X POST -d '{"strings":["world"]}' http://localhost:8080/hello
```

the service will return `["hello", "world"]`

### POST /LoanEligibility

Post:

```sh
curl -X POST 'http://localhost:8080/LoanEligibility' -H 'Content-Type: application/json' \
    -d '{
        "Client": {"age": 43,"salary": 1950, "existing payments": 100},
        "Loan": {"duration": 15,"installment": 180}, 
        "SupremeDirector" : "Yes", 
        "Bribe": 1000
    }'
```

the service will return the decision results.  

If you are using the `docker-compose` script we provided, go to `localhost:3000` and have a look at your dashboards.