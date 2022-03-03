# Kogito Examples

This module contains a number of examples that you can take a look at and try out yourself.  Please take a look at the readme of each individual example for more details on how the example works and how to run it yourself (either locally or on Kubernetes).

Since Kogito aims at supporting both Quarkus and Spring Boot each example usually provides both type of projects.

- Default branch is `stable`, pointing to the latest released version.
- **[You can also check all versions by looking at releases.](https://github.com/kiegroup/kogito-examples/releases/latest)**

## Use alternative Quarkus platforms

The Quarkus quickstarts by default currently use the Quarkus core BOM.

If you want to use an alternative BOM when building the Quarkus quickstarts you can override the `quarkus.platform.*` properties. The following example shows how to set `quarkus.platform.artifact-id` to use the quarkus-universe-bom.

```
mvn -Dquarkus.platform.artifact-id=quarkus-universe-bom clean install
```

Because Kogito and OptaPlanner projects are part of the Quarkus Platform, the same applies also to Kogito BOM and OptaPlanner BOM being used.

By default `org.kie.kogito:kogito-bom` and `org.optaplanner:optaplanner-bom` are used, but, when needed, these can be overridden using Maven properties:
* `kogito.bom.*` for Kogito BOM overrides
* `optaplanner.bom.*` for OptaPlanner BOM overrides

The properties defined in each of the modules and can be overridden as follows:
* Kogito BOM
  ```
  mvn -Dkogito.bom.group-id=io.quarkus.platform -Dkogito.bom.artifact-id=quarkus-kogito-bom -Dkogito.bom.version=2.2.3.Final
  ```
* OptaPlanner BOM
  ```
  mvn -Doptaplanner.bom.group-id=io.quarkus.platform -Doptaplanner.bom.artifact-id=quarkus-optaplanner-bom -Doptaplanner.bom.version=2.2.3.Final
  ```
> Note: It's important to keep BOM versions aligned when overriding. In case of Quarkus Platform this means using a single
> version value for all three (`quarkus.platform.version`, `kogito.bom.version`, `optaplanner.bom.version`) properties.

## Contribution

Everyone is encouraged to contribute to these examples by

* trying it out and providing feedback and ideas for improvement
* create new examples -- **in this case, make sure your PR is against the `main` branch!**
* blogging about it
* using it on conferences and workshops


## Process hello world with scripts

shows most basic use of processes to build up a hello world example

* [on Quarkus](process-scripts-quarkus)
* [on Spring Boot](process-scripts-springboot)


## Process with business rules

shows integration between processes and rules.

* [on Quarkus](process-business-rules-quarkus)
* [on Spring Boot](process-business-rules-springboot)


## Process with Kafka

shows how message start and end events can be easily used to integrate with Apache Kafka to consume where
message name is the Kafka topic and the payload is mapped to process variable. Uses custom types
that are serialized into JSON.

* [on Quarkus](process-kafka-quickstart-quarkus)
* [on Spring Boot](process-kafka-quickstart-springboot)

## Process with Infinispan persistence

shows long running processes with Infinispan persistence so the state of process instances can
be preserved across service restarts.

* [on Quarkus](process-infinispan-persistence-quarkus)
* [on Spring Boot](process-infinispan-persistence-springboot)

## Process with service invocation

shows how easy it is to use local services to be invoked from within process. Allows easy and readable
service invocation use cases to be covered.

* [on Quarkus](process-service-calls-quarkus)
* [on Spring Boot](process-service-calls-springboot)

## Process with REST call

shows REST service invocation and parsing data back to an object instance used as process variable.

* [on Quarkus](process-service-rest-call-quarkus)
* [on Spring Boot](process-service-rest-call-springboot)

## Process with user tasks

shows user task interactions with four eye principle applied

* [on Quarkus](process-usertasks-quarkus)
* [on Spring Boot](process-usertasks-springboot)

## Process with user tasks based on custom life cycle

shows user task interactions with four eye principle applied that supports custom life cycle that allows to
add additional phases to user tasks to indicate other states.

* [on Quarkus](process-usertasks-custom-lifecycle-quarkus)
* [on Spring Boot](process-usertasks-custom-lifecycle-springboot)

## Process with user tasks with security on REST api

shows user task interactions with four eye principle applied with security restrictions on REST api.

* [on Quarkus](process-usertasks-with-security-quarkus)
* [on Spring Boot](process-usertasks-with-security-springboot)

## Process with timers

shows timers (intermediate and boundary) that allows to introduce delays in process execution

* [on Quarkus](process-timer-quarkus)
* [on Spring Boot](process-timer-springboot)

## Other Misc Examples

- Onboarding example combining 1 process and two decision services: see [README.md](onboarding-example/README.md)
- Rules on Quarkus: see [README.md](rules-quarkus-example/README.md)
- Rules on Quarkus with Unit: see [README.md](ruleunit-quarkus-example/README.md)
- Process on Quarkus: see [README.md](process-quarkus-example/README.md)
- Process on Spring Boot: see [README.md](process-springboot-example/README.md)

## Trying the examples with the Kogito Operator

Every example has a directory named `operator` including the YAML files to deploy it using the Kogito Operator in an OpenShift cluster.
Please refer to the [Kogito Documentation](https://docs.jboss.org/kogito/release/latest/html_single/#chap_kogito-deploying-on-openshift)
of how to install the operator to your environment in order to try it there.

## Getting Help
### Issues
- Do you have a [minimal, reproducible example](https://stackoverflow.com/help/minimal-reproducible-example) for your issue?
  - If so, please open a Jira for it in the [Kogito project](https://issues.redhat.com/projects/KOGITO/summary) with the details of your issue and example.
- Are you encountering an issue but unsure of what is going on?
  - Start a new conversation in the Kogito [Google Group](https://groups.google.com/g/kogito-development), or open a new thread in the [Kogito stream](https://kie.zulipchat.com/#narrow/stream/232676-kogito) of the KIE Zulip chat.
  - Please provide as much relevant information as you can as to what could be causing the issue, and our developers will help you figure out what's going wrong.

### Requests
- Do you have a feature/enhancement request?
  - Please open a new thread in the [Kogito stream](https://kie.zulipchat.com/#narrow/stream/232676-kogito) of the KIE Zulip chat to start a discussion there.
