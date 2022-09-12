#!/bin/sh

# commands sequence to remove the timeouts-showcase:

kn service delete timeouts-showcase

kubectl delete sinkbinding sb-timeouts-showcase

kubectl delete trigger callback-event-type-trigger-timeouts-showcase

kubectl delete trigger visa-approved-event-type-trigger-timeouts-showcase

kubectl delete trigger visa-denied-event-type-trigger-timeouts-showcase

