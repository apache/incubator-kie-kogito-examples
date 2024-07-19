# SonataFlow Data Index Use Cases 

Collection of artifacts to test SonataFlow Use Cases TP2.

## Prereqs for all the use cases

1. Minikube installed

We recommend that you start Minikube with the following parameters, note that the `registry` addon must be enabled.
Additionally, the ingress addon is also enabled facilitate data-index querying by using an Ingress. Note that the Ingress provided is just en example to expose the data-index graphiql.
This is not mandatory, and in production environments you must provide your own setup if needed, or even use an OpenShift route, etc.

```shell
minikube start --cpus 4 --memory 10240 --addons registry --addons metrics-server --addons ingress --insecure-registry "10.0.0.0/24" --insecure-registry "localhost:5000"
```

To verify that the registry addon was property added you can execute this command:

```shell
minikube addons list | grep registry
```

```
| registry                    | minikube | enabled ✅   | Google                         |
| registry-aliases            | minikube | disabled     | 3rd party (unknown)            |
| registry-creds              | minikube | disabled     | 3rd party (UPMC Enterprises)   |
```

To verify that the ingress addon was property added you can execute this command:

```shell
minikube addons list | grep ingress
```

```
| ingress                     | minikube | enabled ✅   | Kubernetes                     |
```

2. kubectl installed

3. SonataFlow operator installed if workflows are deployed

To install the operator you can see [SonataFlow Installation](https://sonataflow.org/serverlessworkflow/latest/cloud/operator/install-serverless-operator.html).

## Use cases

This is the list of available use cases:

| Use case                                                | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
|---------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Deploy Data Index Locally](#deploy-data-index-locally) | This use case deploys: <br/> * PostgreSQL Service<br/> * Data Index Service + postgresdb<br/>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| [Use case 1](#use-case-1)                               | This use case deploys: <br/> * PostgreSQL Service<br/> * Data Index Service + postgresdb<br/> *  The `greeting` workflow (no persistence) configured to register the process events on the Data Index Service.                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| [Use case 2](#use-case-2)                               | This use case deploys: <br/> * PostgreSQL Service<br/> * Data Index Service + postgresdb<br/> *  The `greeting` workflow (no persistence) <br/> * The `helloworkflow` (no persistence)<br/> * Workflows are configured to register the process events on the Data Index Service.                                                                                                                                                                                                                                                                                                                                                                                                 |

> **NOTE:** To facilitate the switch between use cases, it's strongly recommended to install each use case in a dedicated namespace.

## Deploy Data Index locally

Example of how to deploy Data Index on Kubernetes that uses a Postgresql DB.

> **NOTE:** The workflow related use cases that needs a data index service already includes this step.

### Procedure

Open a terminal and run the following commands:

1. Create the namespace:

```shell
kubectl create namespace data-index-usecase
```

2. Deploy the Data Index Service:

```shell
kubectl kustomize platforms/data_index_as_platform_service | kubectl apply -f - -n data-index-usecase
```

```
persistentvolumeclaim/postgres-pvc created
deployment.apps/postgres created
service/postgres created
sonataflowplatform.sonataflow.org/sonataflow-platform created
ingress.networking.k8s.io/data-index-service-ingress created
secret/postgres-secrets created
```

This will deploy a Data Index for you in the `data-index-usecase` namespace. (If you don't use a namespace the `default` is used instead)
Data Index will be backed by a Postgres Data Base deployment. **This setup is not intended for production environments** since this simple Postgres Deployment does not scale well. Please see the [Postgres Operator](https://github.com/zalando/postgres-operator) for more information.


To check that the data index is running you can execute this command.

```shell
kubectl get pod -n data-index-usecase
```

```
data-index-service-postgresql-5d76dc4468-69hm6   1/1     Running   0              2m11s
postgres-7f78499688-j6282                        1/1     Running   0              2m11s
```

To access the Data Index, using Minikube you can run:

```shell
minikube ip
```

Example output:
```
192.168.49.2
```

Use the returned ip to access the data-index-service GraphiQL by using the Ingress with a url like this http://192.168.49.2/graphiql/ (that ip might be different in your installation.)

For more information about Data Index and this deployment see [Data Index standalone service](https://sonataflow.org/serverlessworkflow/latest/data-index/data-index-service.html) in SonataFlow guides.

To execute queries see: [Querying Index Queries](#querying-data-index)

3. Clean the use case:

```shell
kubectl delete namespace data-index-usecase
```

## Use case 1

This use case is intended to represent an installation with:

* A singleton Data Index Service with PostgreSQL persistence
* The `greeting` workflow (no persistence), that is configured to register events to the Data Index Service.

### Procedure

Open a terminal and run the following commands:

1. Create the namespace:

```shell
kubectl create namespace usecase1
```

2. Deploy the Data Index Service:

```shell
kubectl kustomize platforms/data_index_as_platform_service | kubectl apply -f - -n usecase1
```

```
persistentvolumeclaim/postgres-pvc created
deployment.apps/postgres created
service/postgres created
sonataflowplatform.sonataflow.org/sonataflow-platform created
ingress.networking.k8s.io/data-index-service-ingress created
secret/postgres-secrets created
```

Give some time for the data index to start, you can check that it's running by executing.

```shell
kubectl get pod -n usecase1
```

```
NAME                                             READY   STATUS    RESTARTS       AGE
data-index-service-postgresql-5d76dc4468-lb259   1/1     Running   0              2m11s
postgres-7f78499688-lc8n6                        1/1     Running   0              2m11s
```

3. Deploy the workflow:

```shell
 kubectl kustomize usecases/usecase1 | kubectl apply -f - -n usecase1
 ```

```
sonataflow.sonataflow.org/greeting created
```

Give some time for the sonataflow operator to build and deploy the workflow.
To check that the workflow is ready you can use this command.

```shell
kubectl get workflow -n usecase1
```

```
NAME       PROFILE   VERSION   URL   READY   REASON
greeting             0.0.1           True    
```

4. Expose the workflow and get the url:

```shell
kubectl patch svc greeting -p '{"spec": {"type": "NodePort"}}' -n usecase1
```

```shell
 minikube service greeting --url -n usecase1
 ```

5. Create a workflow instance:

You must use the URLs calculated in step 4.

```shell
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"name": "John", "language": "English"}'    http://192.168.49.2:32407/greeting
```

**To execute queries and see the workflows information see:** [Querying Index Queries](#querying-data-index)


6. Clean the use case:

```shell
kubectl delete namespace usecase1
```

## Use case 2

This use case is intended to represent an installation with:

* A singleton Data Index Service with PostgreSQL persistence
* The `greeting` workflow (no persistence)
* The `helloworkflow` workflow (no persistence)
* The workflows are configured to register the process events on the Data Index Service.

### Procedure

Open a terminal and run the following commands:

1. Create the namespace:

```shell
kubectl create namespace usecase2
```

2. Deploy the Data Index Service:
3. 
```shell
kubectl kustomize platforms/data_index_as_platform_service | kubectl apply -f - -n usecase2
```

```
persistentvolumeclaim/postgres-pvc created
deployment.apps/postgres created
service/postgres created
sonataflowplatform.sonataflow.org/sonataflow-platform created
ingress.networking.k8s.io/data-index-service-ingress created
secret/postgres-secrets created
```

Give some time for the data index to start, you can check that it's running by executing.

```shell
kubectl get pod -n usecase2
```

```
NAME                                             READY   STATUS    RESTARTS       AGE
data-index-service-postgresql-5d76dc4468-lb259   1/1     Running   0              2m11s
postgres-7f78499688-lc8n6                        1/1     Running   0              2m11s
```

3. Deploy the workflows:

```shell
 kubectl kustomize usecases/usecase2 | kubectl apply -f - -n usecase2
 ```

```
sonataflow.sonataflow.org/greeting created
sonataflow.sonataflow.org/helloworld created
```

Give some time for the sonataflow operator to build and deploy the workflows.
To check that the workflows are ready you can use this command.

```shell
kubectl get workflow -n usecase2
```

```
NAME       PROFILE   VERSION   URL   READY   REASON
greeting             0.0.1           True
helloworld           0.0.1           True        
```

4. Expose the workflows and get the urls:

```shell
kubectl patch svc greeting helloworld  -p '{"spec": {"type": "NodePort"}}' -n usecase2
```

```shell
 minikube service greeting --url -n usecase2
 ```

```shell
 minikube service helloworld --url -n usecase2
 ```

5. Create a workflow instances:

You must use the URLs calculated in step 4.

```shell
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{"name": "John", "language": "English"}'    http://192.168.49.2:32407/greeting
```

```shell
curl -X POST -H 'Content-Type:application/json' -H 'Accept:application/json' -d '{}'    http://192.168.49.2:32327/helloworld
```

**To execute queries and see the workflows information see:** [Querying Index Queries](#querying-data-index)

6. Clean the use case:

```shell
kubectl delete namespace usecase2
```

## Querying Data Index

You can use the public Data Index endpoint to play around with the GraphiQL interface.

### Procedure

This procedure apply to all use cases with that deploys the Data Index Service.

1. Get the Data Index Url:

```shell
minikube ip
```

2. Open the GrahiqlUI

Using the ip returned in 1, open a browser window in the following url http://192.168.49.2/graphiql/, note that IP will be different in your installation, and don't forget to add the last slash "/" to the url, otherwise the GraphiqlUI won't be opened.


To see the process instances information you can execute this query:

```graphql
{
  ProcessInstances {
    id,
    processId,
    processName,
    variables,
    state,
    endpoint,
    serviceUrl,
    start,
    end
  }
}
```

The results should be something like:


```json
{
  "data": {
    "ProcessInstances": [
      {
        "id": "3ed8bf63-85c9-425d-9099-49bfb63608cb",
        "processId": "greeting",
        "processName": "workflow",
        "variables": "{\"workflowdata\":{\"name\":\"John\",\"greeting\":\"Hello from JSON Workflow, \",\"language\":\"English\"}}",
        "state": "COMPLETED",
        "endpoint": "/greeting",
        "serviceUrl": "http://greeting",
        "start": "2023-09-13T06:59:24.319Z",
        "end": "2023-09-13T06:59:24.400Z"
      }
    ]
  }
}
```

To see the jobs instances information, if any, you can execute this query:

```graphql
{
  Jobs {
    id,
    processId,
    processInstanceId,
    status,
    expirationTime,
    retries,
    endpoint,
    callbackEndpoint
  }
}
```

The results should be something like:

```json
{
  "data": {
    "Jobs": [
      {
        "id": "55c7aadb-3dff-4b97-af8e-cc45014b1c0d",
        "processId": "callbackstatetimeouts",
        "processInstanceId": "299886b7-2b78-4965-a701-16783c4162d8",
        "status": "EXECUTED",
        "expirationTime": null,
        "retries": 0,
        "endpoint": "http://jobs-service-postgresql/jobs",
        "callbackEndpoint": "http://callbackstatetimeouts:80/management/jobs/callbackstatetimeouts/instances/299886b7-2b78-4965-a701-16783c4162d8/timers/-1"
      }
    ]
  }
}
```
