## Serverless Workflow GitHub Showcase

In this example we will deploy a GitHub "bot" application that will
react upon a new PR being opened in a given GitHub project. 
The "bot" is implemented via service and event orchestration approach with Kogito 
implementation of the [Serverless Workflow specification](https://github.com/serverlessworkflow/specification).

The image below illustrates an overview of the architecture:

![](docs/github-showcase-architecture-overview.png)

The implementation relies on three services included in this repository:

1. [**Pull Request Checker Workflow**](pr-checker-workflow): it will coordinate the changes in the PR 
opened in a given GitHub repository. Depending on the files changed in the project,
different labels and reviewers will be automatically added in the PR. In the end of 
the workflow, the service will broadcast a "PR Verified" event to the cluster.
2. [**GitHub API Wrapper Service**](github-service): calling the GitHub API as a GitHub Application
requires a token to be generated on a small timeframe. This service generates a valid
token each 5 minutes to make calls to the API. Besides token management, it wraps 
the API and simplifies its interface just for the sake of this example.
3. [**Notification Service**](notification-service): a simple Camel service to interact with the Slack API to 
notify a given channel. 

### Prerequisites

In your local machine you will need:

1. To clone this repository and go to `serverless-workflow-github-showcase` directory (`git clone https://github.com/kiegroup/kogito-examples.git && cd serverless-workflow-github-showcase`)
2. [Java 11 SDK](https://openjdk.java.net/install/)
3. [Maven 3.8.1+](https://maven.apache.org/install.html)
4. [Podman](https://podman.io/getting-started/installation.html) or Docker to build the images
5. `kubectl` or `oc` client

## Deploying on Minikube

You can easily deploy this example on Minikube by using the provided `deploy.sh` script. But first, make sure that you have:

1. Installed Minikube
2. Installed [Knative Quickstart](https://knative.dev/docs/getting-started/quickstart-install/) on your Minikube installation. It adds a new `knative` profile to your cluster, so bear in mind that every command on Minikube must be followed by `-p knative`.
3. Installed JDK 11, Maven, NPM, and Docker in order to build all the parts of the example.

### Minimum Requirements

- Minikube with at least 4 CPU cores
- Minikube RAM of 12GB

Start the tunnel in a separate terminal:

```shell
minikube tunnel -p knative
```

Now just run `./deploy.sh`. It will build all the services, create the Kubernetes object, and push the images to your Minikube's internal registry.


Once the services are deployed, discover the URLs managed by Knative:

```shell
$ kubectl get ksvc -n github-showcase
NAME                   URL                                                                 LATESTCREATED                LATESTREADY                  READY   REASON
event-display          http://event-display.github-showcase.10.101.75.92.sslip.io          event-display-00001          event-display-00001          True    
github-service         http://github-service.github-showcase.10.101.75.92.sslip.io         github-service-00001         github-service-00001         True    
notification-service   http://notification-service.github-showcase.10.101.75.92.sslip.io   notification-service-00001   notification-service-00001   True    
pr-checker-flow        http://pr-checker-flow.github-showcase.10.101.75.92.sslip.io        pr-checker-flow-00001        pr-checker-flow-00001        True    
```


### Deploying the examples

Follow the instructions for each service to try them locally as standalone services
and deploy them in your Kubernetes or OpenShift cluster:

1. [GitHub Service](github-service/README.md)
2. [Notification Service](notification-service/README.md)
3. [PR Checker SW Service](pr-checker-workflow/README.md)

Knative and Kogito will bind them together. :heart:

In case of any problems, please file an issue or reach out to us at the [Kogito Dev Mailing list](https://groups.google.com/forum/?authuser=0#!aboutgroup/kogito-development).

### Cleaning up 

You can easily clean up the demo by deleting the namespace:

```shell script
kubectl delete ns github-showcase
```