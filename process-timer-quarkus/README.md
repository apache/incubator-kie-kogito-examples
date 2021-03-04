# Process Timers examples

## Description

The quickstart project shows use of timer based activities within the process to
allow flexible delays before continuing process execution.


## Solutions

We provide two solutions: 

[Process with human tasks and simple timer. Process interaction given by Kogito Consoles](basic) - This basic example 
preserves data between service restarts and requires the Infinispan server to be available and allows to see a basic timer 
use case with Human tasks involved. 
Includes the docker-compose configuration to start the different services needed for the process execution and the tools:
  * **Kogito Management Console**, to interact with process instances and jobs
  * **Kogito Task Console** to interact with human tasks. 

[Process with different types of timers](extended) -  Explore different types of timers and (intermediate and boundary) and
 optionally use Job Service that allows to externalize time tracking to separate service and by that offload the runtime service.
