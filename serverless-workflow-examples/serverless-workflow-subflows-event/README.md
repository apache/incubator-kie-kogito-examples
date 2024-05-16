# serverless-workflow-subflow-events

This example illustrate how to trigger workflows manually with additional parameters calculated by an initial workflow. 
The workflow responsible for setting up the parameters is executed as the start state.
Then, all possible workflows that might be instantiated with those parameters are registered using `event` state. `exclusive` property is set to false ensuring that the process instance remains active till all possible workflows has been executed. 

## Execution steps. 

Execute main workflow

```
curl --location 'http://localhost:8080/master' \
--header 'Content-Type: application/json' \
--data '{
}'
```

This will return the id and the two properties that are configured by `setup` workflow

```
{
    "id": "ad7e1081-3f05-431e-b246-d9471643fec2",
    "workflowdata": {
        "param1": "This is param1",
        "param2": "This is param2"
    }
}
```

We need to write down the id returned by the previous steps and invoke `workflowA` through a cloud event containing that id as `kogitoprocrefid` attibute. 

```
curl --location 'http://localhost:8080/executeA' \
--header 'Content-Type: application/json' \
--data '{
   "id" : "1",
   "specversion" : "1.0",
   "type" : "executeA",
   "source" : "manual", 
   "data" : {  
     "param4" : "Additional parameter"
   },
   "kogitoprocrefid" : "ad7e1081-3f05-431e-b246-d9471643fec2"
}'
```

The execution of `workflowA` is registered in the quarkus log. 

```
2024-05-14 12:09:10,306 INFO  [org.kie.kog.ser.wor.dev.DevModeServerlessWorkflowLogger] (kogito-event-executor-1) Triggered node 'Start' for process 'workflowA' (8321fbd0-64ee-4e95-91d6-957983a92325)
2024-05-14 12:09:10,306 INFO  [org.kie.kog.ser.wor.dev.DevModeServerlessWorkflowLogger] (kogito-event-executor-1) Triggered node 'doIt' for process 'workflowA' (8321fbd0-64ee-4e95-91d6-957983a92325)
2024-05-14 12:09:10,307 INFO  [org.kie.kog.ser.wor.dev.DevModeServerlessWorkflowLogger] (kogito-event-executor-1) Property 'workflowdata.param3' changed value from: 'null', to: '"This is workflow A"'
2024-05-14 12:09:10,307 INFO  [org.kie.kog.ser.wor.dev.DevModeServerlessWorkflowLogger] (kogito-event-executor-1) Triggered node 'End' for process 'workflowA' (8321fbd0-64ee-4e95-91d6-957983a92325)
```

The main workflow is still active, waiting for execution of workflow B. Lets execute it sending another cloud event. 

```
curl --location 'http://localhost:8080/executeB' \
--header 'Content-Type: application/json' \
--data '{
    "id": "1",
    "specversion": "1.0",
    "type": "executeB",
    "source": "manual",
    "data": {
        "param4": "Additional parameter"
    },
    "kogitoprocrefid": "ad7e1081-3f05-431e-b246-d9471643fec2"
}'
```

We  see in quarkus logs that workflow B is executed and that master workflow is completed, since there are not more waiting events

```
2024-05-14 12:09:10,334 INFO  [org.kie.kog.ser.wor.dev.DevModeServerlessWorkflowLogger] (kogito-event-executor-1) Triggered node 'Start' for process 'workflowB' (5a49f40d-2e54-46fb-8317-b0be12fd9f05)
2024-05-14 12:09:10,334 INFO  [org.kie.kog.ser.wor.dev.DevModeServerlessWorkflowLogger] (kogito-event-executor-1) Triggered node 'doIt' for process 'workflowB' (5a49f40d-2e54-46fb-8317-b0be12fd9f05)
2024-05-14 12:09:10,335 INFO  [org.kie.kog.ser.wor.dev.DevModeServerlessWorkflowLogger] (kogito-event-executor-1) Property 'workflowdata.param3' changed value from: 'null', to: '"This is workflow B"'
2024-05-14 12:09:10,335 INFO  [org.kie.kog.ser.wor.dev.DevModeServerlessWorkflowLogger] (kogito-event-executor-1) Triggered node 'End' for process 'workflowB' (5a49f40d-2e54-46fb-8317-b0be12fd9f05)
2024-05-14 12:09:10,335 INFO  [org.kie.kog.ser.wor.dev.DevModeServerlessWorkflowLogger] (kogito-event-executor-1) Workflow 'workflowB' (5a49f40d-2e54-46fb-8317-b0be12fd9f05) completed
2024-05-14 12:09:10,336 INFO  [org.jbp.pro.cor.eve.EventTypeFilter] (kogito-event-executor-1) This event is subscribed to a message ref processInstanceCompleted:5a49f40d-2e54-46fb-8317-b0be12fd9f05 WorkflowProcessInstance [Id=5a49f40d-2e54-46fb-8317-b0be12fd9f05,processId=workflowB,state=2]
2024-05-14 12:09:10,336 INFO  [org.jbp.pro.cor.eve.EventTypeFilter] (kogito-event-executor-1) This event is subscribed to a message ref processInstanceCompleted:5a49f40d-2e54-46fb-8317-b0be12fd9f05 WorkflowProcessInstance [Id=5a49f40d-2e54-46fb-8317-b0be12fd9f05,processId=workflowB,state=2]
2024-05-14 12:09:10,339 INFO  [org.kie.kog.ser.wor.dev.DevModeServerlessWorkflowLogger] (kogito-event-executor-1) Triggered node 'waitForEventsJoin' for process 'master' (0ee42b37-7106-4157-9d75-00842f1fea45)
2024-05-14 12:09:10,339 INFO  [org.kie.kog.ser.wor.dev.DevModeServerlessWorkflowLogger] (kogito-event-executor-1) Triggered node 'End' for process 'master' (0ee42b37-7106-4157-9d75-00842f1fea45)
2024-05-14 12:09:10,340 INFO  [org.kie.kog.ser.wor.dev.DevModeServerlessWorkflowLogger] (kogito-event-executor-1) Triggered node 'End' for process 'master' (0ee42b37-7106-4157-9d75-00842f1fea45)
```
