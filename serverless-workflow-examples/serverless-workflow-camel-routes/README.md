# Kogito Serverless Workflow Camel Integration

This repository exemplifies the camel add-on that can be used in [Kogito Serverless Workflow projects](https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/) to call custom [Camel Routes](https://camel.apache.org/) within the same application (JVM).

## How this works

You can use any [Camel Quarkus component](https://camel.apache.org/camel-quarkus/2.14.x/reference/index.html) as a [custom Serverless Workflow function](https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/core/custom-functions-support.html). For example, to call a SOAP service using CXF from your workflow you would do:

```json
{
  "id": "camelCustomFunction",
  "version": "1.0",
  "name": "Custom Camel example",
  "description": "This test a custom type can be added as addon in the classpath",
  "start": "start",
  "functions": [
    {
      "name": "callSoap",
      "type": "custom",
      "operation": "camel:direct:numberToWords"
    }
  ],
  "dataInputSchema": "camel.sw.schema.json",
  "states": [
    {
      "name": "start",
      "type": "operation",
      "actions": [
        {
          "functionRef": {
            "refName": "callSoap",
            "arguments": {
              "body": "${ .number }"
            }
          }
        }
      ],
      "stateDataFilter": {
        "output": "${ words = .response[0]}"
      },
      "end": true
    }
  ]
}
```

In the `function` definition, there's a reference to the Camel endpoint defined in the context:

```json
{
  "functions": [
    {
      "name": "callSoap",
      "type": "custom",
      "operation": "camel:direct:numberToWords"
    }
  ]
}
```

In the `src/main/resources/routes`, you can define the Camel route using XML or YAML:

```yaml
- from:
    uri: direct:numberToWords
    steps:
      - bean:
          beanType: java.math.BigInteger
          method: valueOf
      - setHeader:
          name: operationName
          constant: NumberToWords
      - toD:
          uri: cxf://{{com.dataaccess.webservicesserver.url}}?serviceClass=com.dataaccess.webservicesserver.NumberConversionSoapType&wsdlURL=/wsdl/numberconversion.wsdl
```

## Requirements

From the Camel perspective, the only requirement is that the route either return a primitive or a standard Java Bean object that can be marshalled to JSON.

To properly call the Camel endpoint, the workflow operation must defined either one or zero parameter. The parameter name doesn't matter, but you can use `body` by convention:

```json
{
  "functionRef": {
    "refName": "callSoap",
    "arguments": {
      "body": "${ .number }"
    }
  }
}
```

The value is usually a `jq` expression that queries the workflow state for the object that will be used as an argument. It's the Camel route responsibility to handle the JSON data. In the example above, the argument is a primitive type (`int`) to keep the example as simple as possible.

## Use Cases

So when to use this approach?  
Kogito Serverless Workflow essentially has three options for state processing and service/event orchestration:

1. **Event-driven via CloudEvents**. Ideal for an Event-Driven architecture where the services are ready to consume and produce events working in more reactive way.
2. **Sync or Async REST services invocations via OpenAPI/Async API**. There are options also to directly call a REST service in the architecture or ecosystem. Either async and sync methods are supported depending on your requirements.
3. **Internal Service execution or invocation**. Kogito is also a framework to build Java Workflow applications. One can use it to create custom Java services to run a process within the same instance.

Options 1 and 2 runs on a decoupled manner, where the services run as independent instances being orchestrated. Option 3 can be considered as a more coupled integration since the action will run within the same workflow instance.

For the Camel function use case, the route will indeed be coupled to the workflow. So if you understand that your use case needs a specific integration, go for it. Otherwise, I'd recommend using Camel-K and integrate both ends via OpenAPI/REST function calls.