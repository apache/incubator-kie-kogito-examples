**Start point for creating a Task Assigning Service click and go demonstration**

*Before trying to package and configure everything in docker compose, It's recommended that contributors try to start and make run all the demo components locally.*

Please, let's try to keep all the preconfigured ports, by doing this, when all the components are executing we'll have:

* **DataIndex: at** _http://localhost:8180/graphiql/_
* **TaskAssigningService: at** _http://localhost:8380/q/health/live_
* **TaskAssigningService Console: at** _http://localhost:8480/_
* **Task Assigning Processes: at** _http://localhost:8580/q/swagger-ui/
* **Kogito tooling dev UI for executing the tasks: at** _http://localhost:8580/q/dev/org.kie.kogito.runtime-tools-quarkus-extension/tasks_

For having a potential click and go demo **Version 1**, the following services must be running: (steps apply also for executing locally)

1) **Infinispan:** infinispan-server-12.1.7.Final or higher (let's start with 12.1.7.Final)
   
2) **kafka_2.13-2.6.0:** (has been tested and works, higher versions might be explored, specially interesting if zookeeper is removed)
   
3) **DataIndexService:**
   The DataIndexService must be executing accordingly with the installation instructions in:
   https://docs.kogito.kie.org/latest/html_single/#con-data-index-service_kogito-configuring
   The following line shows an example for starting the data-index with the Infinispan variant.
    
----
    java -Dquarkus.infinispan-client.use-auth=true -Dquarkus.infinispan-client.auth-username=myuser -Dquarkus.infinispan-client.auth-password=qwer1234! -Dkogito.protobuf.folder=`pwd`/PROTOS -Ddebug=5008 -jar data-index-service-infinispan-2.0.0-SNAPSHOT-runner.jar
----

4) **TaskAssigningService:**
    The TaskAssigningService must be executing accordingly with the installation instructions in:
    https://docs.kogito.kie.org/latest/html_single/#con-kogito-task-assigning-service_kogito-configuring
    
    For the demo we can use the **task-assigning-service-extension** module, while not mandatory, here we show the extension
    alternatives, so it could be good to use the extended service instead.
   
    For starting the extended server see: task-assigning-service-extension/startTaskAssigningService.sh

5) **TaskAssigningServiceConsole:** demo console in the **task-assigning-service-console** module should ideally be
    executing.
   **see** task-assigning-service-console/README.md and task-assigning-service-console/startTaskAssigningServiceConsole.sh
   
6) **Demo processes:** The task assigning service demo processes in the **task-assigning-processes** module must be running. For **Version 1** let's start
this manually in dev mode. 
   **see** task-assigning-processes/README.md
   
For executing all this locally, it's recomended to go incrementally, a possible path is to: do steps **1)**, **2)**, **3)** and **6)** as a first try. This will enable to create process instances independently of the task assigning service. When this is running, step **4)** can be added, etc.

   


**Finally**:

1) Good start point could be to take a look at the **trusty-demonstration** module, here we have already a docker compose example for this project
2) Note, in our case let's start by using locally generated images for the task assigning service related components (i.e. the extended service and the console) in a second step, quay published images can be used.
3) all the stuff regarding the demo (docker compose files) itself should be located in the task-assigning-demo directory

