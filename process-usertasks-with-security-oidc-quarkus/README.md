# Process user task orchestration with authentication

## Description

The quickstart project shows very typical user task orchestration with keycloak authentication.

## Solutions

We provide two solutions 

[Process with human task with oidc](basic) - The example comes with a basic example with two tasks assigned to human actors via 
groups assignments without preserving data between restarts. This quickstart comes with the basic instructions to create 
a keycloak server, with the necessary configuration and all the steps to try the example

[Process with human tasks with oidc.  Process interaction driven by Kogito Consoles](extended) -  In this case  
the data is preserved between service restarts and requires the Infinispan server to be available. This example explain step 
by step how to complete the process using the Kogito consoles.
Includes the docker-compose configuration to start the different services needed for the process execution 
(Keycloak server included) and the tools:
  * **Kogito Management Console**, to interact with process instances.
  * **Kogito Task Console** to interact with human tasks. 

