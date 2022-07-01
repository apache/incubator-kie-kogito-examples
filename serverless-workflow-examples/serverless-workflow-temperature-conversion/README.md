# Serverless Workflow Temperature Conversion - Services Orchestration Example

In this example we demonstrate a simple workflow that calls two distinct services via REST.
The workflow aims to solve an equation that converts a given Fahrenheit temperature value into Celsius.

The figure below illustrates the architecture:

```
                                      +-------------+  
                                      |             |  
                                      | Subtraction |  
                                   -->| Service     |  
+-------------+               ----/   |             |  
|             |         -----/        +-------------+  
| Temperature |    ----/                               
| Conversion  | --/                                    
| Service     |--\                                     
|             |   ---\                                 
+-------------+       ---\          +-----------------+
                          ---\      |                 |
                              ---\  |  Multiplication |
                                  ->|  Service        |
                                    |                 |
                                    +-----------------+
```

1. The Temperature Conversion service expects a Fahrenheit value as input
2. The workflow then calls the Subtraction service to perform the first computation step
3. The difference of the previous subtraction is used as input to the Multiplication service
4. The resulting product is the converted temperature to Celsius

Please see each service README file to understand how to use and run them locally:

1. [Temperature Conversion Service](conversion-workflow)
2. [Multiplication Service](multiplication-service)
3. [Subtraction Service](subtraction-service)

You can also find alternative OpenAPI endpoint configuration approaches for the Temperature Conversion workflow:
* [Temperature Conversion Service using Full Uri](conversion-workflow-full)
* [Temperature Conversion Service using Spec Title](conversion-workflow-spec)
* [Temperature Conversion Service using Function Name](conversion-workflow-function)