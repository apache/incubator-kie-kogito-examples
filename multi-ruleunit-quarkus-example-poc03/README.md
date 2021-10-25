### POST /2units

Custom REST endpoint to execute CommonUnit and LoanUnit. Then reply with "FindApproved" query.


1st example)

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"loanApplications":[{"id":"ABC10001","amount":5000,"deposit":3000,"applicant":{"age":45,"name":"John","creditScore":1000,"occupationCode":"1021","previousOccupationCode":"1025"}}]}' http://localhost:8080/2units
```

This loan application is approved.

response:

```json
[{"id":"ABC10001","applicant":{"name":"John","age":45,"creditScore":1000,"occupationCode":"1021","previousOccupationCode":"1025","occupationCategory":"A"},"amount":5000,"deposit":3000,"approved":true}]
```

2nd example)

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"loanApplications":[{"id":"ABC10002","amount":5000,"deposit":3000,"applicant":{"age":43,"name":"Paul","creditScore":1000,"occupationCode":null,"previousOccupationCode":"2099"}}]}' http://localhost:8080/2units
```

This loan application is expected to be approved because rule "LargeDepositWithoutCurrentOccupation" in LoanUnit should trigger rule "occupationCategoryA2" in CommonUnit in case of "kbase composition". However, this example is "RuleUnit orchestration" so CommonUnit -> LoanUnit is executed sequentially. So "occupationCategoryA2" is not triggered.



