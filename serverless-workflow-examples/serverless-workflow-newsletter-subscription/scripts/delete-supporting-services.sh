#!/bin/bash

NAMESPACE=newsletter-showcase

# commands sequence to remove the event-display service

kubectl delete trigger event-display-trigger -n $NAMESPACE

kubectl delete trigger event-display-job-event-trigger -n $NAMESPACE

kn service delete event-display -n $NAMESPACE

# commands sequence to remove the jobs-service-postgresql:

kubectl delete deployment jobs-service-postgresql -n $NAMESPACE

kubectl delete service jobs-service-postgresql -n $NAMESPACE

kubectl delete sinkbinding jobs-service-postgresql-sb -n $NAMESPACE

kubectl delete trigger jobs-service-postgresql-cancel-job-trigger -n $NAMESPACE

kubectl delete trigger jobs-service-postgresql-create-job-trigger -n $NAMESPACE

# commands sequence to remove the newsletter-postgres:

kubectl delete deployment newsletter-postgres -n $NAMESPACE

kubectl delete service newsletter-postgres -n $NAMESPACE

kubectl delete secret newsletter-postgres -n $NAMESPACE

# Delete the default broker

kubectl delete broker default -n $NAMESPACE