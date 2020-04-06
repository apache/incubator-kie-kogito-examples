# Kogito Examples

This module contains a number of examples that you can take a look at and try out yourself.  Please take a look at the readme of each individual example for more details on how the example works and how to run it yourself (either locally or on Kubernetes).

Since Kogito aims at supporting both Quarkus and SpringBoot each example usually provides both type of projects.

- Default branch is `stable`, pointing to the latest released version. 
- **[You can also check all versions by looking at releases.](https://github.com/kiegroup/kogito-examples/releases/latest)**


## Contribution

Everyone is encouraged to contribute to these examples by

* trying it out and providing feedback and ideas for improvement
* create new examples -- **in this case, make sure your PR is against the `master` branch!**
* blogging about it
* using it on conferences and workshops


## Kogito hello world with scripts

shows most basic use of processes to build up a hello world example

* [on Quarkus](kogito-scripts-quarkus)
* [on SpringBoot](kogito-scripts-springboot)


## Kogito with business rules

shows integration between processes and rules.

* [on Quarkus](kogito-business-rules-quarkus)
* [on SpringBoot](kogito-business-rules-springboot)


## Kogito with Kafka

shows how message start and end events can be easily used to integrate with Apache Kafka to consume where
message name is the Kafka topic and the payload is mapped to process variable. Uses custom types
that are serialized into JSON.

* [on Quarkus](kogito-kafka-quickstart-quarkus)
* [on SpringBoot](kogito-kafka-quickstart-springboot)

## Kogito with Infinispan persistence

shows long running processes with Infinispan persistence so the state of process instances can
be preserved across service restarts.

* [on Quarkus](kogito-infinispan-persistence-quarkus)
* [on SpringBoot](kogito-infinispan-persistence-springboot)

## Kogito with service invocation

shows how easy it is to use local services to be invoked from within process. Allows easy and readable
service invocation use cases to be covered.

* [on Quarkus](kogito-service-calls-quarkus)
* [on SpringBoot](kogito-service-calls-springboot)

## Kogito with REST call

shows REST service invocation and parsing data back to an object instance used as process variable.

* [on Quarkus](kogito-service-rest-call-quarkus)
* [on SpringBoot](kogito-service-rest-call-springboot)

## Kogito with user tasks

shows user task interactions with four eye principle applied

* [on Quarkus](kogito-usertasks-quarkus)
* [on SpringBoot](kogito-usertasks-springboot)

## Kogito with user tasks based on custom life cycle

shows user task interactions with four eye principle applied that supports custom life cycle that allows to
add additional phases to user tasks to indicate other states.

* [on Quarkus](kogito-usertasks-custom-lifecycle-quarkus)
* [on SpringBoot](kogito-usertasks-custom-lifecycle-springboot)

## Kogito with user tasks with security on REST api

shows user task interactions with four eye principle applied with security restrictions on REST api.

* [on Quarkus](kogito-usertasks-with-security-quarkus)
* [on SpringBoot](kogito-usertasks-with-security-springboot)

## Kogito with timers

shows timers (intermediate and boundary) that allows to introduce delays in process execution

* [on Quarkus](kogito-timer-quarkus)
* [on SpringBoot](kogito-timer-springboot)

## Other Misc Examples

- Onboarding example combining 1 process and two decision services: see [README.md](onboarding-example/README.md)
- Rules on Quarkus: see [README.md](rules-quarkus-example/README.md)
- Rules on Quarkus with Unit: see [README.md](ruleunit-quarkus-example/README.md)
- Business Process on Quarkus: see [README.md](business-process-quarkus-example/README.md)
- Business Process on SpringBoot: see [README.md](business-process-springboot-example/README.md)
