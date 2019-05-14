# Sample OnboardingService

## Description

This sample service illustrates use of submarine components to build a complete
business solution based on business processes and decisions.

It consists of three services

* onboarding - main one which is the entry point for end users
* hr - responsible for strictly hr related activities
* payroll - responsible for payroll related activities

Users usually will interact directly only with onboarding service.


## Installation

Currently there is a need to manually build [submarine-cloud](https://github.com/kiegroup/submarine-cloud)
repository as it's not yet published to any remote maven repository.

Follow instruction of individual services

* [hr](hr/readme.md)
* [payroll](payroll/readme.md)
* [onboarding](onboarding/readme.md)


## Usage

### post /onboarding

To start a onboarding for given employee use following sample request

```sh
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"employee" : {"firstName" : "Mark", "lastName" : "Test", "personalId" : "xxx-yy-zzz", "birthDate" : "1995-12-10T14:50:12.123+02:00", "address" : {"country" : "US", "city" : "Boston", "street" : "any street 3", "zipCode" : "10001"}}}' http://localhost:8080/onboarding                                                                                                
```

Note that subsequent calls for the same employee will result in failed validation as only one time
employee can be onboarded.

You can inspect [swagger docs](http://localhost:8080/docs/swagger.json) to learn more about the service.
