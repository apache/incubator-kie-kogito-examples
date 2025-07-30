# Sample On Boarding Services

## Description

This example illustrates how you can build a complete onboarding solution by combining multiple services (based on
business processes and decisions).

It consists of three independent services:

* onboarding - main service where you can start onboarding new employees (defined as a set of 3 business processes)
* hr - a decision service responsible for HR-related activities (using DRL rules)
* payroll - a decision service responsible for payroll-related activities (using DMN)

Users will typically only interact with the main onboarding service, but this one relies on the other two to manage some
of the work.

You can run the example in any of the following configurations:

* As local services using quarkus dev mode
* As local services using native images
* Running on top of Kubernetes
* Running on top of knative

## Installing and Running

### Prerequisites

You will need:

- Java 17+ installed
- Environment variable JAVA_HOME set accordingly
- Maven 3.9.9+ installed

When using native image compilation, you will also need:

- [GraalVM 19.1.1](https://github.com/oracle/graal/releases/tag/vm-19.1.1) installed
- Environment variable GRAALVM_HOME set accordingly
- Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be
  installed too. You also need 'native-image' installed in GraalVM (using 'gu install native-image'). Please refer
  to [GraalVM installation documentation](https://www.graalvm.org/docs/reference-manual/aot-compilation/#prerequisites)
  for more details.

### Installation

Please follow the instruction for each of the individual services. It is recommended to install them in given order.

* [hr](hr/README.md)
* [payroll](payroll/README.md)
* [onboarding quarkus](onboarding-quarkus/README.md)

## Example Usage

Once the services are up and running, please use the sample request as described in the onboarding service readme to
start a new onboarding.

## Running on Minikube

Once you have deployed the three services (see the READMEs above), you can run the following command to expose
the `onboarding-service` via Node Port (the default option already configured for you):

```shell
minikube service --url onboarding-service
```

You should see the URL in the terminal. Use it to make the calls to the `onboarding-service`