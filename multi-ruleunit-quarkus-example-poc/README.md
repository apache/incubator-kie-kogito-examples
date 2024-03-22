### POST /find-invalid-applicant

Returns applicants who are considered as invalid by ApplicantValidationUnit rules:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"loanApplications":[{"id":"ABC10001","amount":2000,"deposit":100,"applicant":{"age":45,"name":"John","creditScore":20}}, {"id":"ABC10002","amount":500,"deposit":100,"applicant":{"age":25,"name":"Paul","creditScore":200}}, {"id":"ABC10015","amount":1000,"deposit":100,"applicant":{"age":12,"name":"George","creditScore":200}}, {"id":"ABC10020","amount":5000,"deposit":100,"applicant":{"age":30,"name":"Ringo","creditScore":200}}]}' http://localhost:8080/find-invalid-applicant
```

George is invalid due to his age. John is invalid due to his creditScore.

Example response:
```json
[{"name":"George","age":12,"creditScore":200,"invalid":true},{"name":"John","age":45,"creditScore":20,"invalid":true}]
```


### POST /find-approved

Returns approved loan applications by LoanUnit rules:

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"loanApplications":[{"id":"ABC10001","amount":2000,"deposit":100,"applicant":{"age":45,"name":"John","creditScore":20}}, {"id":"ABC10002","amount":500,"deposit":100,"applicant":{"age":25,"name":"Paul","creditScore":200}}, {"id":"ABC10015","amount":1000,"deposit":100,"applicant":{"age":12,"name":"George","creditScore":200}}, {"id":"ABC10020","amount":5000,"deposit":100,"applicant":{"age":30,"name":"Ringo","creditScore":200}}]}' http://localhost:8080/find-approved
```

Ringo was rejected but other 3 loan applications are approved because LoanUnit doesn't validate applicant's age and creditScore.

Example response:

```json
[{"id":"ABC10002","applicant":{"name":"Paul","age":25,"creditScore":200,"invalid":false},"amount":500,"deposit":100,"approved":true},{"id":"ABC10001","applicant":{"name":"John","age":45,"creditScore":20,"invalid":false},"amount":2000,"deposit":100,"approved":true},{"id":"ABC10015","applicant":{"name":"George","age":12,"creditScore":200,"invalid":false},"amount":1000,"deposit":100,"approved":true}]
```

### POST /2units

Custom REST endpoint to execute ApplicantValidationUnit and LoanUnit. Then reply with "FindApproved" query.

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"loanApplications":[{"id":"ABC10001","amount":2000,"deposit":100,"applicant":{"age":45,"name":"John","creditScore":20}}, {"id":"ABC10002","amount":500,"deposit":100,"applicant":{"age":25,"name":"Paul","creditScore":200}}, {"id":"ABC10015","amount":1000,"deposit":100,"applicant":{"age":12,"name":"George","creditScore":200}}, {"id":"ABC10020","amount":5000,"deposit":100,"applicant":{"age":30,"name":"Ringo","creditScore":200}}]}' http://localhost:8080/2units
```

Only Paul is approved because it passes both ApplicantValidationUnit and LoanUnit rules.

Example response:

```json
[{"id":"ABC10002","applicant":{"name":"Paul","age":25,"creditScore":200,"invalid":false},"amount":500,"deposit":100,"approved":true}]
```

