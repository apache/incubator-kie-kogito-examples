# Kogito Serverless Workflow - Newsletter Subscription Showcase

In this example, you will see a Newsletter Subscription use case described with the [Serverless Workflow specification](https://serverlessworkflow.io/).

<!-- add image -->

The figure below illustrates the overall architecture of this use case.

1. Once a new subscription request comes, the flow will evaluate if it's not already subscribed.
2. Case not, it will attempt to subscribe the new user and wait for the confirmation.
3. Once a new event containing the confirmation arrives, the flow will resume and subscribe the new user.
4. By the end, a new event containing the details of the subscription is broadcasted in the environment, so other actors can react upon it.

<!-- add image -->

This example demonstrates a few features powered by the Kogito implementation of the Serverless Workflow specification:

1. REST Services calls via OpenAPI definitions
2. Pause and resume of a given workflow instance
3. Consuming and producing CloudEvents

In a Knative environment, the services involved in this use case can be scaled to zero and resume from the exact stage it was, saving cluster resources in the process.

## Using Quarkus Dev Services

You can use the Workflow Instance management dev service when in Quarkus Dev Mode (`quarkus dev` from the [subscription-flow](subscription-flow) module root) to visualie the details of a given workflow instance:

<!-- add image -->

## The User Interface

<!-- add image -->

## Running on Knative

Alternatively, you can run this whole example on Knative. Instead of using Kafka, we are going to leverage the Knative Eventing Broker to abstract the broker implementation for us.

In this example we use a regular, in-memory, broker. Feel free to adapt the example to use other brokers implementations.

### Preparing your environment

1. Install [minikube](https://minikube.sigs.k8s.io/docs/start/)
2. Install Knative using the [quickstarts](https://knative.dev/docs/getting-started/) since a DNS will be configured for you.
3. Run `eval $(minikube -p minikube docker-env --profile knative)` to build the images in your internal Minikube registry.
4. Run `mvn clean install -Pknative`. All resources needed to run the example will be generated for you.

Deploy the services with the following command:

```shell
# the namespace name is very important. If you decide to change the namespace, please be update the query-answer-service Knative properties.
$ kubectl create ns newsletter-showcase
# install the subscription-flow and the Postgres database
$ kubectl apply -f subscription-flow/target/kubernetes/knative.yml -n newsletter-showcase
$ kubectl apply -f subscription-flow/target/kubernetes/kogito.yml -n newsletter-showcase
# install the subscription-service 
$ kubectl apply -f subscription-service/target/kubernetes/knative.yml -n newsletter-showcase
```

And you are done! To play around with the example UI, first discover the URLs managed by Knative:

```shell
$ kubectl get ksvc -n newsletter-showcase

NAME                   URL                                                                  LATESTCREATED                LATESTREADY                  READY   REASON
event-display          http://event-display.newsletter-showcase.127.0.0.1.sslip.io          event-display-00001          event-display-00001          True    
subscription-flow      http://subscription-flow.newsletter-showcase.127.0.0.1.sslip.io      subscription-flow-00001      subscription-flow-00001      True    
subscription-service   http://subscription-service.newsletter-showcase.127.0.0.1.sslip.io   subscription-service-00002   subscription-service-00002   True    
```

The `URL` column has the applications' endpoint.

Expose the URLs in your local environment. In a separated terminal, run:

```shell
# you will be asked for your admin password
$ minikube tunnel --profile knative
```

Open the URLs in your browser and try playing with your services scaling to 0.

Note that even when the pod is scaled back after a short period of time, your data remains there. That's the power of a stateful Kogito Serverless Workflow!
