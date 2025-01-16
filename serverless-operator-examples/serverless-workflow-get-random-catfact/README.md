# {product_name} Operator - Get Random Cat Fact Example

## Description

The goal of this example is to showcase use of [{product_name} plugin for Knative CLI](https://sonataflow.org/serverlessworkflow/main/testing-and-troubleshooting/kn-plugin-workflow-overview.html) for local development and subsequent deployment of finished {product_name} application.

### Use Case

This example is doing basic decision based on input provided to the workflow. If the input matches a string, workflow will query a specific endpoint and store the result, otherwise it uses a static string.
The example contains workflow definition, necessary application.properties and an openAPI spec file to be able to query external service.

### Prerequisites

1. Install [{product_name} plugin for Knative CLI](https://sonataflow.org/serverlessworkflow/main/testing-and-troubleshooting/kn-plugin-workflow-overview.html)
2. Install the [{product_name} Operator](https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/cloud/operator/install-serverless-operator.html)
3. Checkout this example locally

### Run the example in local environment

In order to ensure the developed workflows does what it is meant to do, `run` command allows users to spin up a container in development mode on localhost. A [management console](https://sonataflow.org/serverlessworkflow/main/testing-and-troubleshooting/quarkus-dev-ui-extension/quarkus-dev-ui-overview.html) is available for users to play with the workflows.

1. Navigate to the root directory of the example
2. Execute `kn workflow run`
3. Navigate to `http://localhost:8080/q/dev-ui/org.apache.kie.sonataflow.sonataflow-quarkus-devui/workflows` to access the management console
4. You can modify the project, any changes will be detected and the container will update

### Deploy the example to cluster

Once the workflow is doing what it is expected to do, the `deploy` command allows users to create deployments of the workflow on the targeted cluster. To deploy a workflow application, [{product_name} plugin for Knative CLI](https://sonataflow.org/serverlessworkflow/main/testing-and-troubleshooting/kn-plugin-workflow-overview.html) relies on configuration in `<home_directory>/.kube/config`. If you cluster has managed access, users need to login prior to the use of CLI. 

Please note that by default, the `deploy` command deploys the workflow in `dev` mode. This mode allows you to examine the deployment in actual k8s cluster environment, with same development features as `run` command does locally. Follow the guide to understand how to deploy the workflow in different modes, suitable for post-development scenarios.

1. Create a namespace for your deployment `oc create namespace catfactexample`
2. Navigate to the root directory of the example
3. Execute `kn workflow deploy -n catfactexample`
4. Access your cluster, you should see new deployment in `catfactexample` namespace. Note the Route associated with the deployment. For example `<WORKFLOW_GENERATED_ROUTE_URL>`
5. Management console can be accessed on the `<WORKFLOW_GENERATED_ROUTE_URL>/q/dev-ui/org.apache.kie.sonataflow.sonataflow-quarkus-devui/workflows`

[NOTE]
====
To retrieve the `<WORKFLOW_GENERATED_ROUTE_URL>` in different environments, please follow the guides provided for the environment. Here are examples from [Minikube](https://minikube.sigs.k8s.io/docs/handbook/accessing/) and [Kind](https://kind.sigs.k8s.io/docs/user/ingress/).
====

### Undeploy the example from cluster

In order to get rid of the deployment, `undeploy` command allows user to cleanup the deployed resources.

1. Navigate to the root directory of the example.
2. Execute `kn workflow undeploy -n catfactexample`
3. The namespace is now clean and without any resources.

### Deploy the example to cluster with different configuration

By default, `deploy` command generates the Kubernetes definitions during the deployment. In order to customize these files, use `--custom-generated-manifests-dir=./manifests` parameter to store the generated Kubernetes manifests. Once modified, use `--custom-manifests-dir=./manifests` to use the already generated manifests.

In this example we will make necesarry adjustments to deploy the workflow in `preview` mode. This mode offers deployment setup closer to production and allows user to check and validate different custom configuration and supporting services setup.
Please note that in  `preview` mode the management console is no longer available.

1. Navigate to the root directory of the example.
2. Execute `kn workflow deploy -n catfactexample --custom-generated-manifests-dir=./manifests`. This command will generate Kubernetes YAML definitions based on your workflow in this location.
3. Undeploy the `kn workflow deploy -n catfactexample --custom-generated-manifests-dir=./manifests`
4. Navigate to the `/.manifests` folder and modify the files. For example, add `sonataflow.org/profile: preview` under `annotations`.
5. Navigate to root directory of the example.
6. Execute `kn workflow deploy -n catfactexample --custom-manifests-dir=./manifests`
7. The workflow is now deployed with `sonataflow.org/profile: preview` annotation.

Another option is to use `gen-manifest` to create the Kubernetes manifests, run `kn workflow gen-manifest --help` to see the documentation for this command. The procedure to deploy is as follows:

1. Navigate to the root directory of the example.
2. Execute `kn workflow gen-manifest -n catfactexample --profile=preview --custom-generated-manifests-dir=./manifests`
3. Execute `kn workflow deploy -n catfactexample --custom-manifests-dir=./manifests`
4. The workflow is now deployed with `sonataflow.org/profile: preview` annotation.

If the application deployment uses the `preview` profile, the route is no longer generated for you. It is up to the user to generate, whether the workflow should be exposed to public or not. 
In order to trigger the workflows try sending a HTTP POST request to the endpoint of your workflow application. For example `curl -X POST -H 'Content-Type: application/json' -H 'Accept: application/json' -d '{"fact":"random"}' http://<URL_TO_HOST>/getcatfactinformation` where `URL_TO_HOST` depends on the environment you are in.