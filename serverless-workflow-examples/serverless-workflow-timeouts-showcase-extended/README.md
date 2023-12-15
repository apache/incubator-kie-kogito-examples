### Preparing your environment

1. Install [minikube](https://minikube.sigs.k8s.io/docs/start/)
2. Install Knative using the [quickstarts](https://knative.dev/docs/getting-started/) since a DNS will be configured for you.

> **NOTE:** Every time you restart your minikube installation, you must activate the knative profile, and be sure that you have enabled the minikube tunnel for this profile after minikube has started.
> You can do this by executing these commands:
> ```shell
> minikube start -p knative
> minikube tunnel -p knative
> ```

### Building the project

Once the minikube environment is running, open a terminal window, go to the serverless-workflow-timeouts-showcase-extended directory, and execute these commands to be sure the generated images are stored in the minikube internal registry. 

```shell
eval $(minikube -p knative docker-env)

mvn clean package -Pknative
```

### Creating the namespace

```shell
# The namespace name is very important to ensure all the services that compose the showcase can interact.
kubectl create ns timeouts-showcase
```
> **NOTE:** In cases where you need to clean the deployed workflows, to start again, or simply release the
> resources on your minikube installation see: [Showcase cleaning](#showcase-cleaning)
>

### Deploying the database

To deploy the postgresql database used by the showcase you must execute this command:

```shell
kubectl apply -f kubernetes/timeouts-showcase-database.yml -n timeouts-showcase

# After executing the command, you will see an output like this:

secret/timeouts-showcase-database created
deployment.apps/timeouts-showcase-database created
service/timeouts-showcase-database created
```

### Deploying the Jobs Service

To deploy the Jobs Service you must execute this command:

```shell
kubectl apply -f kubernetes/jobs-service-postgresql.yml -n timeouts-showcase

# After executing the command, you will see an output like this:

service/jobs-service-postgresql created
deployment.apps/jobs-service-postgresql created
trigger.eventing.knative.dev/jobs-service-postgresql-create-job-trigger created
trigger.eventing.knative.dev/jobs-service-postgresql-cancel-job-trigger created
sinkbinding.sources.knative.dev/jobs-service-postgresql-sb created
```

### Querying the Jobs Service logs (optional step)

To see the Jobs Service logs you can execute this procedure:

```shell
kubectl get pod -n timeouts-showcase | grep jobs-service-postgresql

# After executing the command, you will see an output like this:

jobs-service-postgresql-56d9668b4b-k4v87            1/1     Running   0             72s

# Note that it might take some time for the service to start, and the pod name can be different in your installation.

# To see the jobs service logs you can execute this command:

kubectl logs jobs-service-postgresql-56d9668b4b-k4v87 -n timeouts-showcase

__  ____  __  _____   ___  __ ____  ______ 
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2022-08-18 10:34:47,902 jobs-service-postgresql-56d9668b4b-k4v87 INFO  [org.kie.kogito.jobs.service.json.JacksonConfiguration:-1] (main) Jackson customization initialized.
2022-08-18 10:34:48,544 jobs-service-postgresql-56d9668b4b-k4v87 INFO  [org.flywaydb.core.internal.license.VersionPrinter:-1] (main) Flyway Community Edition 8.5.13 by Redgate
2022-08-18 10:34:48,545 jobs-service-postgresql-56d9668b4b-k4v87 INFO  [org.flywaydb.core.internal.license.VersionPrinter:-1] (main) See what's new here: https://flywaydb.org/documentation/learnmore/releaseNotes#8.5.13
2022-08-18 10:34:48,545 jobs-service-postgresql-56d9668b4b-k4v87 INFO  [org.flywaydb.core.internal.license.VersionPrinter:-1] (main) 
2022-08-18 10:34:48,678 jobs-service-postgresql-56d9668b4b-k4v87 INFO  [org.flywaydb.core.internal.database.base.BaseDatabaseType:-1] (main) Database: jdbc:postgresql://timeouts-showcase-database:5432/postgres (PostgreSQL 13.4)
2022-08-18 10:34:48,727 jobs-service-postgresql-56d9668b4b-k4v87 INFO  [org.flywaydb.core.internal.command.DbMigrate:-1] (main) Current version of schema "public": 2.0.1
2022-08-18 10:34:48,728 jobs-service-postgresql-56d9668b4b-k4v87 INFO  [org.flywaydb.core.internal.command.DbMigrate:-1] (main) Schema "public" is up to date. No migration necessary.
2022-08-18 10:34:49,065 jobs-service-postgresql-56d9668b4b-k4v87 INFO  [io.quarkus:-1] (main) jobs-service-postgresql 999-SNAPSHOT on JVM (powered by Quarkus 2.11.2.Final) started in 2.040s. Listening on: http://0.0.0.0:8080
2022-08-18 10:34:49,065 jobs-service-postgresql-56d9668b4b-k4v87 INFO  [io.quarkus:-1] (main) Profile prod activated. 
2022-08-18 10:34:49,065 jobs-service-postgresql-56d9668b4b-k4v87 INFO  [io.quarkus:-1] (main) Installed features: [agroal, cdi, flyway, jdbc-postgresql, kafka-client, narayana-jta, oidc, reactive-pg-client, reactive-routes, resteasy, resteasy-jackson, security, smallrye-context-propagation, smallrye-fault-tolerance, smallrye-health, smallrye-openapi, smallrye-reactive-messaging, smallrye-reactive-messaging-http, smallrye-reactive-messaging-kafka, swagger-ui, vertx]
2022-08-18 10:34:49,241 jobs-service-postgresql-56d9668b4b-k4v87 INFO  [org.kie.kogito.jobs.service.scheduler.JobSchedulerManager:-1] (executor-thread-0) Loading scheduled jobs completed !
```

### Deploying the Data Index Service (optional step)

To deploy the Data Index Service you must execute this command:

```shell
kubectl apply -f kubernetes/data-index-service-postgresql.yml -n timeouts-showcase

# After executing the command, you will see an output like this:

service/data-index-service-postgresql created
deployment.apps/data-index-service-postgresql created
trigger.eventing.knative.dev/data-index-service-postgresql-processes-trigger created
trigger.eventing.knative.dev/data-index-service-postgresql-jobs-trigger created
```

### Deploying the workflows

To deploy the example workflows you must execute these commands:

```shell
kubectl apply -f target/kubernetes/knative.yml -n timeouts-showcase

kubectl apply -f target/kubernetes/kogito.yml -n timeouts-showcase

# After executing the commands you will see an output like this:

service.serving.knative.dev/timeouts-showcase-extended created
serviceaccount/timeouts-showcase-extended created
rolebinding.rbac.authorization.k8s.io/timeouts-showcase-extended-view created

trigger.eventing.knative.dev/callback-event-type-trigger-timeouts-showcase-extended created
trigger.eventing.knative.dev/event1-event-type-trigger-timeouts-showcase-extended created
trigger.eventing.knative.dev/event2-event-type-trigger-timeouts-showcase-extended created
trigger.eventing.knative.dev/visa-approved-event-type-trigger-timeouts-showcase-extended created
trigger.eventing.knative.dev/never-trigger-timeouts-showcase-extended created
trigger.eventing.knative.dev/visa-denied-event-type-trigger-timeouts-showcase-extended created
trigger.eventing.knative.dev/wake-up-event-type-trigger-timeouts-showcase-extended created
sinkbinding.sources.knative.dev/sb-timeouts-showcase-extended created
```

To get the URL to access the service you can execute this command:

```shell
kn service list -n timeouts-showcase | grep timeouts-showcase-extended

# After executing the command you will see an output like this:

timeouts-showcase-extended   http://timeouts-showcase-extended.timeouts-showcase.10.98.134.49.sslip.io   timeouts-showcase-extended-00002   2m28s   3 OK / 3     True    
```

Note that the output above might be different in your installation, and the IP numbers in the URL can be different.

### Executing the workflows via REST APIs

To execute the following commands you must use the http://timeouts-showcase-extended.timeouts-showcase.10.98.134.49.sslip.io corresponding to your installation.

Execute the following command to create a new `switch_state_timeouts` workflow instance:

```shell
curl -X 'POST' \
  'http://timeouts-showcase-extended.timeouts-showcase.10.98.134.49.sslip.io/switch_state_timeouts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{}'

# The command will produce an output like this:

{"id":"2e8e1930-9bae-4d60-b364-6fbd61128f51","workflowdata":{}}
```

If you execute the following command during the first 30 seconds after the SW instance was created, you'll get the following results:
```shell
curl -X 'GET' 'http://timeouts-showcase-extended.timeouts-showcase.10.98.134.49.sslip.io/switch_state_timeouts'

# The command will produce an output like this, which indicates that the workflow is waiting for an event to arrive.

[{"id":"2e8e1930-9bae-4d60-b364-6fbd61128f51","workflowdata":{}}]
```

If you execute the previous command 30+ seconds after the SW instance was created, you'll get an empty array as 
result, which means that the SW has timed-out.
```shell
# empty array as result.
[]
```

To execute the `callback_state_timeouts` workflow you must execute this command:

```shell
curl -X 'POST' \
  'http://timeouts-showcase-extended.timeouts-showcase.10.98.134.49.sslip.io/callback_state_timeouts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{}'
```

Similar to `switch_state_timeouts` you can wait for 30+ seconds to check the SW was timed-out.

To execute the `event_state_timeouts` workflow you must execute this command:

```shell
curl -X 'POST' \
  'http://timeouts-showcase-extended.timeouts-showcase.10.98.134.49.sslip.io/event_state_timeouts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "workflowdata": {}
}'
```

Finally, you can execute the following command to create a new `workflow_timeouts` workflow instance:

```shell
curl -X 'POST' \
  'http://timeouts-showcase-extended.timeouts-showcase.10.98.134.49.sslip.io/workflow_timeouts' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "workflowdata": {}
}'
```

You can also verify the timeouts functioning following this procedure:
```shell
kubectl get pod -n timeouts-showcase | grep timeouts-showcase-extended

# The command above will produce an output like this, where timeouts-showcase-00001-deployment-56dcc74c77-jttv5 
# corresponds to the Pod executing the service.

timeouts-showcase-extended-00002-deployment-68cb59d6ff-c67nc         2/2     Running   0             10m
```

Finally, you can execute the following command to see the logs corresponding to that Pod. And see the log traces
corresponding to the created and timed-out serverless workflow instance.

```shell
kubectl logs timeouts-showcase-extended-00002-deployment-68cb59d6ff-c67nc -n timeouts-showcase

# The command will produce an output like this, where you'll find the log traces produced by the switch-state-timeouts 
# workflow instance.

switch-state-timeouts: 2e8e1930-9bae-4d60-b364-6fbd61128f51 has started.
switch-state-timeouts: 2e8e1930-9bae-4d60-b364-6fbd61128f51 has finalized. No decision was made. The switch state did not receive any event, and the timeout has overdue.
```

### Timeouts showcase UI
The timeouts showcase provides a simple UI that can be used to create workflow instances, and also send them the expected events.
To execute the UI you must:
1) Follow the steps described at the beginning of this document to get the timeouts showcase running
2) Execute the following command to determine the URL of the timeouts showcase in your local environment:

```shell
kn service list -n timeouts-showcase

# After executing the command you will see an output like this:

NAME                         URL                                                                         LATEST                             AGE     CONDITIONS   READY   REASON
timeouts-showcase-extended   http://timeouts-showcase-extended.timeouts-showcase.10.98.134.49.sslip.io   timeouts-showcase-extended-00002   9m53s   3 OK / 3     True    
```

3) Open a browser window with the url above: http://timeouts-showcase-extended.timeouts-showcase.10.98.134.49.sslip.io

#### Switch-state-timeouts tab
In this tab, you can create and complete instances of the `switch-sate-timeouts` workflow.

![](docs/SwitchStateTimeoutsTab.png)

#### Callback-state-timeouts tab
In this tab, you can create and complete instances of the `callback-sate-timeouts` workflow.

![](docs/CallbackStateTimeoutsTab.png)

#### Event-state-timeouts tab
In this tab, you can create and complete instances of the `event-sate-timeouts` workflow.

![](docs/EventStateTimeoutsTab.png)

#### Workflow-timeouts tab
In this tab, you can create and complete instances of the `workflow-timeouts` workflow.

![](docs/WorkflowTimeoutsTab.png)

> **NOTE:** Remember that example workflows are configured with timeouts, which means that, if you create a workflow instance
> and execute no action, when the timeout is met, if you refresh the data, the given instance won't be shown anymore. This last is perfectly fine, since the workflow might have finished because of the timeout overdue.
> 
> We recommend that you test the different workflows and actions one by one, at the same time that you query the timeouts-showcase logs to verify the traces generated by the workflows.

### Querying the Data Index Service (optional)

If you have deployed the Data Index Service, as shown in the [Deploying the Data Index Service (optional step)](#deploying-the-data-index-service-optional-step), the information about the executed workflows and jobs is recorded by that service, and you can query it by executing GraphQL queries.
A user-friendly way to execute these queries is by using the Data Index Service GraphQL UI, that you can access by following these steps:

1) Get the Data Index Service cluster IP address.

```shell
kubectl get service -n timeouts-showcase | grep data-index-service-postgresql

# After executing the command you will see an output like this, where you can see the service IP
data-index-service-postgresql              ClusterIP      10.110.95.69    
```

2) Open a browser window using the IP calculated in the previous step: http://10.110.95.69/graphiql/ and execute your queries.

![](docs/DataIndexGraphQLUI.png)

### Showcase cleaning

To remove the installed services from your minikube installation you can use the following command:

```shell
# Note: this command might take some seconds.
kubectl delete namespace timeouts-showcase    
```

### Known issues

At the time of writing this guide, it was detected that if you stop, and start, your minikube installation, there are times when an initialization error like the one below is produced.

```shell
error execution phase addon/coredns: unable to create deployment: Internal error occurred: failed calling webhook "sinkbindings.webhook.sources.knative.dev": 
failed to call webhook: Post "https://eventing-webhook.knative-eventing.svc:443/sinkbindings?timeout=10s": dial tcp 10.97.131.18:443: connect: connection refused
```

This is a "minikube + knative quickstart plugin" initialization error, rather than a showcase issue. If this happens in your installation you must remove the knative profile from your minikube installation and repeat the showcase installation procedure.

```shell
# stop the minikube 
minikube stop -p knative

# remove the knative profile
minikube delete -p knative

# re-install the knative quickstart plugin
kn-quickstart minikube

# repeat the steps to install the showcase...
```