# Multiplication Function

This is a small, lightweight service built on top of Quarkus to perform
multiplication operations.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
mvn compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
mvn package
```
It produces the `quarkus-app/quarkus-run.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
mvn package -Dquarkus.package.jar.type=uber-jar
```

The application is now runnable using `java -jar target/multiplication-service-{version}-runner.jar`.

## Creating a native executable

You can create a native executable using:
```shell script
mvn package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell script
mvn package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/multiplication-service-{version}-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

## Testing the Application

Simply run the application with:

```shell
$ mvn clean quarkus:dev -Dquarkus.http.port=8282
```

In a new terminal, execute a `curl` command to the root path:

```shell
$ curl -X POST \
  -H 'Content-Type:application/json'\
  -H 'Accept:application/json' \
  -d '{"leftElement" : "5", "rightElement": "2" }' \
  http://localhost:8282/
```

And expect a reply like this:

```json
{"multiplication":{"leftElement":5.0,"rightElement":2.0,"product":10.0}}
```
