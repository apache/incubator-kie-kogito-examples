# Trusty demonstation

This is a demonstration of the trusty AI tracing capabilities.

NOTE: In order to play with the demo, ensure that you are on the stable branch or on the branch of a specific release (
for example the branch for the release `0.17` is `0.17.x`).
The `main` branch is aligned to the latest changes in all the repositories. This means that `main` might be using some
new operator features not included in the release that we use in this demo: don't use it!

## Requirements

- docker version > 19.03.12
- java version > 11
- maven version > 3.8.1
- docker-compose version > 1.25.2

Note: also previous versions of `docker` and `docker-compose` might work, but they were not tested.

## Build your kogito runtime application

In this example, we will use the `dmn-tracing-quarkus` application that you can find in the root of this repository.
This kogito application is using the `tracing-addon` so to export the tracing information that will be used by the
trusty and explainability services for further analysis.

Navigate under the folder `kogito-examples/kogito-quarkus-examples/dmn-tracing-quarkus` and run

```bash
mvn clean package -DskipTests
```

Copy the generated dashboards from `dmn-tracing-quarkus/target/classes/META-INF/resources/monitoring/dashboards/*` to
the directory `trusty-demonstration/docker-compose/grafana/provisioning/dashboards`.

Now you need to have an account on a remote hub like `quay` or `dockerhub` for example. Assuming that you have an
account on `quay` and you are using `docker`, build the image with

```bash
docker build --tag quay.io/<your_namespace>/dmn-tracing-quarkus:1.0.0 .
```

replacing the string `<your_namespace>` with your namespace (i.e. your username).

Push the image

```bash
docker push quay.io/<your_namespace>/dmn-tracing-quarkus:1.0.0
```

## Deploy the services with docker-compose

Switch to the current directory `trusty-demonstration/docker-compose` and edit the `docker-compose.yml` file and replace
the image for the service `kogito-app` with the image tag you've just
created (`quay.io/<your_namespace>/dmn-tracing-quarkus:1.0.0`).

Then simply start it with

```bash 
docker-compose up
```

The applications will be available in few minutes at the following addresses:

- Kogito application: [http://localhost:8080](http://localhost:8080). You can use the swagger-ui at the
  address [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui) and execute a simple request for example
  with the following payload

```JSON
{"Bribe": 1000,"Client": {"age": 43,"existing payments": 100,"salary": 1950},"Loan": {"duration": 15,"installment": 180}, "SupremeDirector": "Yes"}
```

- AuditUI: [http://localhost:1338](http://localhost:1338).
- Grafana: [http://localhost:3000](http://localhost:3000).

For more info about the AuditUI, you can have a look at
the [official documentation](https://docs.jboss.org/kogito/release/latest/html_single/#proc-audit-console-using_kogito-dmn-models).
Note that the documentation covers the scenario of the AuditUI deployed with the Kogito Operator on Openshift, but the
part specific for the AuditUI (alias for trusty-ui) is valid for this scenario as well.