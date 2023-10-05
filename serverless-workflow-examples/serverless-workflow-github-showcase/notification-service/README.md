## Notification Service

This service will listen to specific [CloudEvents](https://github.com/cloudevents/spec) published by our workflow and post
messages in a specific Slack channel using the event data as input.

### Creating a new Slack App

Like in the GitHub service procedure, we are going to need an API key from a third party
service. 

Go to [Slack API](https://api.slack.com/apps) page and create a new app 
(or you can reuse one you already have instead).

Once you create the app, go to the "Incoming WebHooks" menu and copy the "WebHook URL". 

Install the app in one of the Workspaces you have and create a "github-showcase" channel
there for the service to send some messages. Since it's a demo, you don't want to annoy people with lame 
bot messages. :)

### Trying the service locally

Once you have the Slack App created and the WebHook, it's time to try the application locally.

This service is just a plain Quarkus application with the [Camel Slack component](https://camel.apache.org/components/latest/slack-component.html) to communicate
with the Slack API.

Copy the WebHook URL in the `src/main/resources/application.properties` file:

```properties
# URL details not shown
org.acme.examples.sw.notification.slack.incoming=https://hooks.slack.com/services/(...)
```

Run the application with:

```shell script
$ mvn clean quarkus:dev
```

This service listens to the `/` (root) path for messages in [CloudEvents format](https://github.com/cloudevents/spec/blob/v1.0/spec.md#example), but 
we added the `/plain` endpoint as well for testing purposes. Sending a request to this
endpoint will post a message in the `github-showcase` channel:

```shell script
curl -X POST "http://localhost:8080/plain" -H  "Content-Type: text/plain" -d "this is a test message"
``` 

As always, we included the Swagger UI in the service, access it at http:localhost:8080/swagger-ui/. 

### Deploying on Kubernetes

> **HEADS UP!** delete the Slack WebHook from the `application.properties` file. 
> We're going to build an image from the source, you don't want your credentials to be exposed to the world!! 

> **IMPORTANT! :warning:** we assume you have read the prerequisites section in the main
> [README file](../README.md). Please follow those instructions before continuing.

**Heads up!** This service will be deployed as a Knative Service instead of a regular Kubernetes
Deployment.

To make things easier there is a [script in this directory](deploy-kubernetes.sh) to generate the template
files, build the application and the image, and then deploy it to your Kubernetes cluster.

**IMPORTANT!** You **must** be authenticated to the target Kubernetes cluster as a **cluster administrator** for this script
to work.

You can run the script once and all the required files will be generated  in the `kubernetes` directory, 
and the image will be published to your Quay.io account.

Fill the value for the variables as shown below and run the script:

```shell script
# the script accepts positional arguments as following:
QUAY_NAMESPACE=
SLACK_WEBHOOK=

./deploy-kubernetes.sh $QUAY_NAMESPACE $SLACK_WEBHOOK
```

You should see a similar output like this:

<details><summary>Build logs</summary>
```
// build logs surpressed
---> Building and pushing image using tag quay.io/your_namespace/notification-service:latest
STEP 1: FROM adoptopenjdk:11-jre-hotspot
STEP 2: RUN mkdir -p /opt/app/lib
--> Using cache 26183c5ad8a51a030030a250db0c99e649fdd9668ef4766d0b66782d0dad7573
STEP 3: COPY target/notification-service-2.0.0-SNAPSHOT-runner.jar /opt/app
--> 2a5b658411b
STEP 4: COPY target/lib/*.jar /opt/app/lib
--> 5fedac21977
STEP 5: CMD ["java", "-jar", "/opt/app/notification-service-2.0.0-SNAPSHOT-runner.jar"]
STEP 6: COMMIT quay.io/your_namespace/notification-service:latest
--> afe502d1940
afe502d1940d65f151c051008bb2057344607408c192787a726399d23d90c2d3
Getting image source signatures
Copying config afe502d194 done  
Writing manifest to image destination
Copying config afe502d194 [--------------------------------------] 0.0b / 6.2KiB
Writing manifest to image destination
Writing manifest to image destination
Storing signatures
---> Applying objects to the cluster in the namespace kogito-github.
configmap/notification-service-properties unchanged
secret/slack-ids configured
service.serving.knative.dev/notification-service configured
broker.eventing.knative.dev/default unchanged
trigger.eventing.knative.dev/notification-trigger unchanged
```
</details>

To verify if the service have been correctly deployed run:

```
$ kubectl get ksvc notification-service  -n kogito-github
  
NAME                   URL                                                     LATESTCREATED                LATESTREADY                  READY   REASON
notification-service   http://notification-service.kogito-github.example.com   notification-service-9mgww   notification-service-9mgww   True    
```

The `READY` column should be true.

#### Exposing the service on Minikube

If you're running on another cluster than Minikube, the service's route exposed by Knative Serving probably is accessible to you.
On Minikube there are some additional steps to be made. 

Run a new terminal window:

```shell script
minikube tunnel
```

Leave the process executing and then execute:

```shell script
./expose-on-minikube.sh
```

This script will fetch the Minikube IP exposed by the `tunnel` command and add the route to your local `/etc/hosts` file.

You can then access the service via the service URL:

```
$  kubectl get ksvc notification-service  -n kogito-github --output jsonpath="{.status.url}"

http://notification-service.kogito-github.example.com
```

As we did when running through the `jar` file, we can access the Swagger UI and play around with the API: 

http://notification-service.kogito-github.example.com/swagger-ui

The first query may take a little time to return since Knative will start the service's pod on demand. 
After some time the pod will just terminate. 

Congratulations! The Notification service is now available in the cluster ready to be consumed by the Kogito Workflow.

### Cleaning up!

See the project root's [README](./README.md) documentation.
