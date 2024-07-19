# Kogito Serverless Workflow - gRPC example

The goal of this example is to implement a simple gRPC example for serverless workflow

You first need to run the gRPC server, check /serverless-workflow-greeting-server-rpc-quarkus readme in order to do that. 
Once the gRPC server is running, you can run the quarkus application following serverless-workflow-greeting-client-rpc-quarkus readme.

## Running the example with Containers

To build both server and client containers use:

```bash
$ mvn clean package -Dcontainer
```

Once the build finishes, access the `docker` directory and execute:

```bash
$ docker-compose up
```

Then the application can be used as described [here](serverless-workflow-greeting-client-rpc-quarkus/README.md#submit-a-request)

