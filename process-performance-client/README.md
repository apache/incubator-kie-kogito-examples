# Process + Quarkus example

## Description

A Java client to run with process-performance-quarkus and process-performance-springboot in order to gather performance differences between starting a process through a REST invocation or through a Kafka message. 

In order to run the test you need to run process-performance-quarkus or process-performance-spring boot first.

Once the server is ready, you can execute ``MainRunner`` which will dispatch as many start requests as specified in the constructor of ``RequestDispatcherRunner``

Internally, the code registers on  _done_  Kafka topic to find out when a process has really ended and calculate the difference between the moment the dispatch to start the process was sent and the instant in which the end message is received. 

Collected performance data (essentially the amount of time to complete a batch of processes)  will be printed in the console as regular logs. The expected result is that the times from regular REST and Kafka are on the same order of magnitude. 

