# Process user task orchestration

## Description

The quickstart project shows very typical user task orchestration.

## Solutions

We provide two solutions: 

[Process with human task](basic) - The example comes with a basic example with two tasks assigned to human actors via 
groups assignments without preserving data between restarts.

[Process with human tasks.  Process interaction driven by Kogito Consoles](extended) -  In this case  
the data is preserved between service restarts and requires the Infinispan server to be available. This example explain step 
by step how to complete the process using the Kogito consoles.
Includes the docker-compose configuration to start the different services needed for the process execution and the tools:
  * **Kogito Management Console**, to interact with process instances and jobs
  * **Kogito Task Console** to interact with human tasks. 

