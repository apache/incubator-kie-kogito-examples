# Apache KIE Examples

This repository contains a number of examples for [Drools](https://github.com/apache/incubator-kie-drools), [Kogito](https://github.com/apache/incubator-kie-kogito-runtimes) and [Sonataflow](https://sonataflow.org) that you can take a look at and try out yourself.  Please take a look at the readme of each individual example for more details on how the example works and how to run it yourself.

Apache KIE Examples showcase use cases, features, applications, know-how's of everything our community's technology has to offer.

Apache KIE Examples are currently updated after a stable release.

Current, default branch is `stable-10.0.0`, pointing to the latest released version.

## Use alternative Quarkus platforms with examples

The examples that use Quarkus use the Quarkus core BOM by default.

If you want to use an alternative BOM when building the Apache KIE Quarkus quickstarts you can override the `quarkus.platform.*` properties. The following example shows how to set `quarkus.platform.artifact-id` to use the quarkus-universe-bom.

```
mvn -Dquarkus.platform.artifact-id=quarkus-universe-bom clean install
```
Currently, only [Drools](https://github.com/apache/incubator-kie-drools) is part of the Quarkus Platform.
> Note: Integration of Kogito and Sonataflow is in progress.

## Getting started

. Clone this repository
```
git clone git@github.com:apache/incubator-kie-kogito-examples.git
```
.Navigate to the root of the example's directory
```
cd ./incubator-kie-kogito-examples/serverless-workflow-examples/serverless-workflow-funqy
```
.Open the README.md and follow its instructions

> Found an issue? Please report it [here](https://github.com/apache/incubator-kie-kogito-examples/issues/new?template=bug_report.yml) and we will take a look.

## Contribution

Everyone is encouraged to contribute to these examples by

* trying it out and providing feedback and ideas for improvement
* create new examples by send a [pull-request](https://github.com/apache/incubator-kie-kogito-examples/compare/main...main) against main branch
* blogging about it
* using it on conferences and workshops


## Process hello world with scripts

shows most basic use of processes to build up a hello world example

* [on Quarkus](kogito-quarkus-examples/process-scripts-quarkus)
* [on Spring Boot](kogito-springboot-examples/process-scripts-springboot)


## Process with business rules

shows integration between processes and rules.

* [on Quarkus](kogito-quarkus-examples/process-business-rules-quarkus)
* [on Spring Boot](kogito-springboot-examples/process-business-rules-springboot)


## Process with Kafka

shows how message start and end events can be easily used to integrate with Apache Kafka to consume where
message name is the Kafka topic and the payload is mapped to process variable. Uses custom types
that are serialized into JSON.

* [on Quarkus](kogito-quarkus-examples/process-kafka-quickstart-quarkus)
* [on Spring Boot](kogito-springboot-examples/process-kafka-quickstart-springboot)

## Process with Infinispan persistence

shows long running processes with Infinispan persistence so the state of process instances can
be preserved across service restarts.

* [on Quarkus](kogito-quarkus-examples/process-infinispan-persistence-quarkus)
* [on Spring Boot](kogito-springboot-examples/process-infinispan-persistence-springboot)

## Process with service invocation

shows how easy it is to use local services to be invoked from within process. Allows easy and readable
service invocation use cases to be covered.

* [on Quarkus](kogito-quarkus-examples/process-service-calls-quarkus)
* [on Spring Boot](kogito-springboot-examples/process-service-calls-springboot)

## Process with REST call

shows REST service invocation and parsing data back to an object instance used as process variable.

* [on Quarkus](kogito-quarkus-examples/process-rest-service-call-quarkus)
* [on Spring Boot](kogito-springboot-examples/process-rest-service-call-springboot)

## Process with user tasks

shows user task interactions with four eye principle applied

* [on Quarkus](kogito-quarkus-examples/process-usertasks-quarkus)
* [on Spring Boot](kogito-springboot-examples/process-usertasks-springboot)

## Process with user tasks based on custom life cycle

shows user task interactions with four eye principle applied that supports custom life cycle that allows to
add additional phases to user tasks to indicate other states.

* [on Quarkus](kogito-quarkus-examples/process-usertasks-custom-lifecycle-quarkus)
* [on Spring Boot](kogito-springboot-examples/process-usertasks-custom-lifecycle-springboot)

## Process with user tasks with security on REST api

shows user task interactions with four eye principle applied with security restrictions on REST api.

* [on Quarkus](kogito-quarkus-examples/process-usertasks-with-security-quarkus)
* [on Spring Boot](kogito-springboot-examples/process-usertasks-with-security-springboot)

## Process with timers

shows timers (intermediate and boundary) that allows to introduce delays in process execution

* [on Quarkus](kogito-quarkus-examples/process-timer-quarkus)
* [on Spring Boot](kogito-springboot-examples/process-timer-springboot)

## Serverless Workflow Getting Started

A Serverless Workflow greeting service with both JSON and YAML workflow definitions

* [on Quarkus](serverless-workflow-examples/serverless-workflow-greeting-quarkus)

## Serverless Workflow with events

A Serverless Workflow service for processing job applicant approvals and that showcases event-driven services.

* [on Quarkus](serverless-workflow-examples/serverless-workflow-events-quarkus)

## Serverless Workflow with service calls

A Serverless Workflow service for returning country information

* [on Quarkus](serverless-workflow-examples/serverless-workflow-service-calls-quarkus)

## Serverless Workflow GitHub showcase

A Serverless Workflow service that works as a Github bot application, which reacts upon a new PR being opened in a given GitHub project.

* [on Quarkus](serverless-workflow-examples/serverless-workflow-github-showcase)

## Other Misc Examples

- Onboarding example combining 1 process and two decision services: see [README.md](kogito-quarkus-examples/onboarding-example/README.md)
- Rules on Quarkus: see [README.md](kogito-quarkus-examples/rules-quarkus-helloworld/README.md)
- Rules on Quarkus with Unit: see [README.md](kogito-quarkus-examples/ruleunit-quarkus-example/README.md)
- Process on Quarkus: see [README.md](kogito-quarkus-examples/process-quarkus-example/README.md)
- Process on Spring Boot: see [README.md](kogito-springboot-examples/process-springboot-example/README.md)
- Trusty on Quarkus: see [README.md](kogito-quarkus-examples/trusty-tracing-devservices/README.md)

## Getting Help
### Issues
- Do you have a [minimal, reproducible example](https://stackoverflow.com/help/minimal-reproducible-example) for your issue?
  - If so, please open a new issue in [Github](https://github.com/apache/incubator-kie-kogito-examples/issues) repository with the details of your issue and affected example.
- Are you encountering an issue but unsure of what is going on?
  - Start a new threads in our [Apache KIE Zulip chat](https://kie.zulipchat.com/). Please use the channel which the example is rellated to.
  - Please provide as much relevant information as you can as to what could be causing the issue and a reproducer. Our developers will help you figure out what's going wrong.

### Requests
- Do you have a feature/enhancement request?
  - Please open a new thread in the [Kogito stream](https://kie.zulipchat.com/#narrow/stream/232676-kogito) of the KIE Zulip chat to start a discussion there.
