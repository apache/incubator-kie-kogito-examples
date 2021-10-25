### Single loan application test

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"loanApplications":[{"id":"ABC10002","amount":500,"deposit":100,"applicant":{"age":25,"name":"Paul","creditScore":200}}]}' http://localhost:8080/2units
```

Currently this poc02 takes only one loanApplication request because TwoUnitsService takes one LoanApplication object. This is more looks like a reactive use case. Of course we can improve TwoUnitsEndpoint to iterate LoanApplication objects but I leave it for simplicity now.

```json
{"id":"ABC10002","applicant":{"name":"Paul","age":25,"creditScore":200,"invalid":false},"amount":500,"deposit":100,"approved":true}
```