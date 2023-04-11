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
org.kogito.examples.sw.notification.slack.incoming=https://hooks.slack.com/services/(...)
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

### Running on knative

> **IMPORTANT! :warning:** we assume you have read the prerequisites section in the main
> [README file](../README.md). Please follow those instructions before continuing.

Run `mvn clean install -Pknative`

Deploy the service with the following command:

 ```shell
 # install the notification-service 
 $ kubectl apply -f notification-service/target/kubernetes/knative.yml -n github-showcase
 ```
To verify if the service have been correctly deployed run:

```
 $ kubectl get ksvc notification-service  -n github-showcase
 NAME                   URL                                                                  LATESTCREATED                LATESTREADY                  READY   REASON
notification-service   http://notification-service.github-showcase.10.104.64.247.sslip.io   notification-service-00001   notification-service-00001   True    

```
The `READY` column should be true.

#### Exposing the service on Minikube

Execute the following command to expose the knative service

Run a new terminal window:

```shell script
minikube tunnel
```
Leave the process executing and then execute:

```shell
 # expose the github-service 
 $ kubectl expose deployment notification-service --name=notification-service-external --type=LoadBalancer --port=8080 -n github-showcase
 ```

You can then access the service via the service URL:

```
$   kubectl get ksvc notification-service  -n github-showcase --output jsonpath="{.status.url}"
    http://notification-service.github-showcase.10.104.64.247.sslip.io
```

As we did when running through the `jar` file, we can access the Swagger UI and play around with the API:

http://notification-service.github-showcase.10.104.64.247.sslip.io/q/swagger-ui

The first query may take a little time to return since Knative will start the service's pod on demand.
After some time the pod will just terminate.

Congratulations! The Notification service is now available in the cluster ready to be consumed by the Kogito Workflow.

### Cleaning up!

See the project root's [README](./README.md) documentation.
