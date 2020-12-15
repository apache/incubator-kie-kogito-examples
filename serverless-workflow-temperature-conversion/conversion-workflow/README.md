# Temperature Conversion Workflow

```shell
curl -X POST \
    -H 'Content-Type:application/json' \
    -H 'Accept:application/json' \
    -d '{"workflowdata" : { "fahrenheit": 100  } }' \
    http://localhost:8080/fahrenheit_to_celsius\?forceSync\=true | jq .
    
{
  "id": "54498577-b367-43c7-b840-d0ada6f2b02a",
  "workflowdata": {
    "fahrenheit": 100,
    "subtractValue": "32.0",
    "multiplyValue": "0.5556",
    "subtraction": {
      "leftElement": 100,
      "rightElement": 32,
      "difference": 68
    },
    "multiplication": {
      "leftElement": 68,
      "rightElement": 0.5556,
      "product": 37.7808
    }
  }
}

```