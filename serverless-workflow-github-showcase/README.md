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

> **IMPORTANT!** Before proceeding, please make sure you have everything listed in this section ready.

You may use CRC or Minikube if you don't have a cluster available with cluster admin rights.
Or you can ask an administrator to install the prereqs for you.

To deploy this example in your Kubernetes/OpenShift cluster, you will need:

1. A [Quay.io](https://quay.io/repository/) account
2. A Kubernetes/OpenShift namespace to deploy the example: `kubectl create ns kogito-github`
3. [**Istio**](https://istio.io/docs/setup/install/istioctl/) installed. 
You can follow the [Knative documentation](https://knative.dev/development/install/installing-istio) for a very basic and simple installation.
4. **Knative** Serving and Eventing components installed. 
We recommend [installing the Knative Operator](https://knative.dev/docs/install/knative-with-operators/) and install the rest of the components
through it as described in their documentation.
5. **Kogito Operator** installed in the namespace `kogito-github`. [Download the latest release](https://github.com/kiegroup/kogito-cloud-operator/releases), and run: `NAMESPACE=kogito-github ./hack/install.sh`

In your local machine you will need:

1. To clone this repository: `https://github.com/kiegroup/kogito-examples.git && cd serverless-workflow-github-showcase`
2. [Java 11 SDK](https://openjdk.java.net/install/)
3. [Maven 3.6.3+](https://maven.apache.org/install.html)
4. [Podman](https://podman.io/getting-started/installation.html) or Docker to build the images
5. `kubectl` or `oc` client

### Deploying the examples

Follow the instructions for each service to try them locally as standalone services
and deploy them in your Kubernetes or OpenShift cluster:

1. [GitHub Service](github-service/README.md)
2. [Notification Service](notification-service/README.md)
3. [PR Checker SW Service](pr-checker-workflow/README.md)

Knative and Kogito will bind them together. :heart: