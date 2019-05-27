# Drools + Quarkus Hello World

## Installing and Running

- Prerequisites: install qs-playground 8.0-SNAPSHOT

- Compile and Run

    ```
     mvn clean compile quarkus:dev    
    ```

- Native Image (requires JAVA_HOME to point to a valid GraalVM)

    ```
    mvn package -Pnative
    ```
  
  native executable (and runnable jar) generated in `target/`

## Examples

### Hello World

Point to http://localhost:8080/hello for Drools hello world

### More complex example

- post 

```sh
curl -d '{"name":"edo", "age":32}' -H "Content-Type: application/json" \
    -X POST http://localhost:8080/persons                                                                                                    ~
```

- http://localhost:8080/persons/all returns a list of all persons

- http://localhost:8080/persons/adults returns a list of all adults

### Manual Deploy on Openshift

#### Build Container on docker
```sh
docker build -t kogito-examples/drools-quarkus .
docker images | grep drools-quarkus
```

#### Deploy on Openshift
By default will be created under project called "My Project"
```sh
kubectl create -f kubernetes/deployment.yml 
kubectl create -f kubernetes/service.yml 
```
In the pod's log you could see
```
2019-03-07 15:51:40,720 INFO  [io.quarkus] (main) Quarkus 0.11.0 started in 0.019s. Listening on: http://[::]:8080
2019-03-07 15:51:40,720 INFO [io.quarkus] (main) Installed features: [cdi, resteasy]
```

Let's go to expose the service
```
oc expose service drools-quarkus
```
this create a yaml file and the route for us on openshift, like this (in routes section on My Project)
 ```yaml
 
 apiVersion: route.openshift.io/v1
 kind: Route
 metadata:
   annotations:
     openshift.io/host.generated: 'true'
   creationTimestamp: '2019-02-20T10:25:59Z'
   labels:
     app: drools-quarkus
   name: drools-quarkus
   namespace: myproject
   resourceVersion: '30743'
   selfLink: /apis/route.openshift.io/v1/namespaces/myproject/routes/drools-quarkus
   uid: ea2676d6-34f9-11e9-bd97-08002709a920
 spec:
   host: drools-quarkus-myproject.192.168.99.109.nip.io
   port:
     targetPort: http
   to:
     kind: Service
     name: drools-quarkus
     weight: 100
   wildcardPolicy: None
 status:
   ingress:
     - conditions:
         - lastTransitionTime: '2019-02-20T10:25:59Z'
           status: 'True'
           type: Admitted
       host: drools-quarkus-myproject.192.168.99.109.nip.io
       routerName: router
       wildcardPolicy: None

 ```
 ```
 oc get route
 
  NAME             HOST/PORT                                        PATH             SERVICES       PORT      TERMINATION   WILDCARD
  drools-quarkus   drools-quarkus-myproject.192.168.99.109.nip.io   drools-quarkus   http           None
  ```

  Your address will be
  http://drools-quarkus-myproject.192.168.99.109.nip.io/hello
  
  
