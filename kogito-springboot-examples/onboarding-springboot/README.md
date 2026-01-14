# Sample On Boarding Services

## Description

This example illustrates how you can build a complete onboarding solution by combining multiple services (based on
business processes and decisions).

It consists of three independent services:

* onboarding - main service where you can start onboarding new employees (defined as a set of 3 business processes)
* hr - a decision service responsible for HR-related activities (using DRL rules)
* payroll - a decision service responsible for payroll-related activities (using DMN)

Note that both the hr and payroll services are implemented in the corresponding [quarkus based example](../../kogito-quarkus-examples/onboarding-example/).

Users will typically only interact with the main onboarding service, but this one relies on the other two to manage some
of the work.

This is the main onboarding service that exposes a single REST endpoint to onboard new employees.

The service is defined using a combination of 3 business processes to describe the steps required to onboard new
employees. This includes one main process that serves as the overall entry point and two sub-processes to handle the
interaction with HR and payroll.

## Installing and Running

### Prerequisites

You will need:

- Java 11+ installed
- Environment variable JAVA_HOME set accordingly
- Maven 3.9.11+ installed

### Installation

Please follow the instruction for each of the individual services. It is recommended to install them in given order.

* [hr](../../kogito-quarkus-examples/onboarding-example/hr/README.md)
* [payroll](../../kogito-quarkus-examples/onboarding-example/payroll/README.md)
* [onboarding spring boot](README.md)

### Compile and Run in Local Dev Mode

```
mvn clean package spring-boot:run
```

### Running with persistence enabled

Kogito runtime supports multiple persistence types, including Infinispan. In order to use the Infinispan based
persistence, you need to have a Infinispan server installed and available over the network. The default configuration,
expects the server to be running on:

```
infinispan.remote.server-list=localhost:11222
```

If you need to change it, you can do so by updating the application.properties file located in src/main/resources.

You can install Infinispan server by downloading version 12.x from
the [official website](https://infinispan.org/download/).

Once Inifispan is up and running you can build this project with `-Ppersistence` to enable additional processing during
the build. Next you start it in exact same way as without persistence.

This extra profile in maven configuration adds additional dependencies needed to work with infinispan as persistent
store.

## Usage

Once the services are up and running, you can use the following example to interact with the service.

### POST /onboarding

To start a new onboarding for the given employee, use following sample request:

```sh
curl -X POST -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8080/onboarding
```

This will onboard the given employee by the contacting the hr service (for validation, adding an employee identifier and
manager, etc.) and the payroll service (for adding information about payroll, taxation and vacation days). It will
return all employee information (including the newly generated information from the hr and payroll service) and the
status of the onboarding request. Note that subsequent calls with the same information (employee personal ID) will
result in a failed status as an employee can only be onboarded once.

Please note that the first execution of this request might take slightly longer, as some lazy loading and initialization
of various components might still be required. Subsequent calls should be a lot faster though.

## Deployment to Kubernetes, OpenShift or Minikube

This project is configured to run with JKube Maven plugin. You can simply run:

```shell
# make sure that the docker env from minikube is enabled locally
$ eval $(minikube -p minikube docker-env)

# build the service, the image and deploy it on Minikube:
$ mvn clean install k8s:deploy -Pkubernetes
```

If you wish to only generate the image, you can use `k8s:build` instead. To generate the Kubernetes resources
use `k8s:resource`. See the [official JKube documentation](https://www.eclipse.org/jkube/docs/kubernetes-maven-plugin)
for more information.

> NOTE: If you're targeting a Kubernetes or OpenShift cluster, use the resources created on `target/classes/META-INF/jkube` directory.

You can use the default configuration to deploy on Minikube and start exploring the examples right way. If you're
planning to deploy on an actual cluster, review the [src/main/jkube](src/main/jkube) files to make sure the permissions
added to the service are fine.

## Deploying to Knative

Unfortunately, JKube does not support Knative natively. What you can do is use this example as a reference:

````yaml
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: onboarding-springboot
spec:
  template:
    spec:
      containers:
        - image: kogito/onboarding-springboot:latest
          imagePullPolicy: IfNotPresent
          serviceAccountName: onboarding-springboot
          ports:
            - containerPort: 8080
              name: http
              protocol: TCP
````

Run the commands bellow to deploy the Knative service:

```shell
# make sure that the docker env from minikube is enabled locally
$ eval $(minikube -p minikube docker-env)

# build the service, the image and the other resources:
$ mvn k8s:resource -Pkubernetes

# apply the other resources
$ kubectl apply -f target/classes/META-INF/jkube/onboarding-springboot-service.yml
$ kubectl apply -f target/classes/META-INF/jkube/onboarding-springboot-serviceaccount.yml
$ kubectl apply -f target/classes/META-INF/jkube/onboarding-springboot-view-rolebinding.yml

# apply the knative service resource
$ kubectl apply -f <path-to-file>.yaml
```

> NOTE: Please install the Knative Serving platform before attempting to run the command above.

In addition, following permissions should be added to the default service account so the service discovery can properly
happen:

```sh
oc policy add-role-to-group view system:serviceaccounts:onboarding-service -n istio-system
oc policy add-role-to-group knative-serving-core system:serviceaccounts:onboarding-service -n default
```

If using [Knative Serving Operator](https://github.com/knative/serving-operator) on OpenShift 4.x, the permissions that
need to be set are

```sh
oc adm policy add-role-to-user knative-serving-core system:serviceaccount:myproject:onboarding-service -n default
oc adm policy add-role-to-user view system:serviceaccount:myproject:onboarding-service -n istio-system
```

## Testing on Minikube

Once you have deployed the three services, you can run the following command to expose the `onboarding-springboot` via
Node Port (the default option is already configured for you):

```shell
minikube service --url onboarding-springboot
```

You should see the URL in the terminal. Use it to make the calls to the `onboarding-springboot`
