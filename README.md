# Apache KIE Examples

This repository contains a number of examples for [Drools](https://github.com/apache/incubator-kie-drools), [jBPM](https://www.jbpm.org), [Kogito](https://github.com/apache/incubator-kie-kogito-runtimes) and [Sonataflow](https://sonataflow.org) that you can take a look at and try out yourself.  Please take a look at the readme of each individual example for more details on how the example works and how to run it yourself.

Apache KIE Examples showcase use cases, features, applications, know-how's of everything our community's technology has to offer.

Apache KIE Examples are currently updated after a stable release.

## Branches

Current stable branch is `10.0.x`, pointing to the latest released `10.0.0` version.

Current development branch is `main`.

## Use alternative Quarkus platforms with examples

The examples that use Quarkus use the Quarkus core BOM by default.

If you want to use an alternative BOM when building the Apache KIE Quarkus quickstarts you can override the `quarkus.platform.*` properties. The following example shows how to set `quarkus.platform.artifact-id` to use the quarkus-universe-bom.

```
mvn -Dquarkus.platform.artifact-id=quarkus-universe-bom clean install
```
Currently, only [Drools](https://github.com/apache/incubator-kie-drools) is part of the Quarkus Platform.
> Note: Integration of Kogito and Sonataflow is in progress.

## Getting started with the examples

1. Clone this repository
```
git clone git@github.com:apache/incubator-kie-kogito-examples.git
```
2. Navigate to the root of the example's directory
```
cd ./incubator-kie-kogito-examples/serverless-workflow-examples/serverless-workflow-funqy
```
3. Open the README.md and follow its instructions

> Found an issue? Please report it [here](https://github.com/apache/incubator-kie-kogito-examples/issues/new?template=bug_report.yml) and we will take a look.

## Contribution

Everyone is encouraged to contribute to these examples by:

* trying it out and providing feedback and ideas for improvement
* creating new examples by sending a [pull-request](https://github.com/apache/incubator-kie-kogito-examples/compare/main...main) against main branch
* blogging about it
* using it on conferences and workshops

## Getting Help
### Issues
- Do you have a [reproducer](https://stackoverflow.com/help/minimal-reproducible-example) for your issue?
  - If so, please open a new issue in [Github](https://github.com/apache/incubator-kie-kogito-examples/issues) repository with the details of your issue and affected example.
- Are you encountering an issue but unsure of what is going on?
  - Start a new thread in our [Apache KIE Zulip chat](https://kie.zulipchat.com/). Please use the channel which the example is related to.
  - Please provide as much relevant information as you can as to what could be causing the issue and a reproducer. Our developers will help you figure out what's going wrong.

### Requests
- Do you have a feature/enhancement request?
  - Please open a new thread in the [Kogito stream](https://kie.zulipchat.com/#narrow/stream/232676-kogito) of the KIE Zulip chat to start a discussion there.
