# jBPM + SpringBoot timer example

## Description

A sample project for demonstration of jBPM SpringBoot interoperability with Jobs service.


## Installing and Running

This sample can be ran just on OpenShift 4 instance as it requires communication with Job service.
Use Kogito operator to deploy this example and instantiate also Jobs service. Kogito operator will take care of configuring the example deployment to successfully connect to the Jobs service.

## Test your application

Generated application comes with sample test process that allows you to verify if the application is working as expected. Simply execute following command to try it out

```sh
curl -X POST -H 'Content-Type: application/json' -i 'http://example-route-on-openshift/timer'
```

Once successfully invoked you should see "Before timer" and "After timer" in the console of the running application.

