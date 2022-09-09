#!/bin/sh

# commands sequence to remove the jobs-service-postgresql:

kubectl delete deployment jobs-service-postgresql

kubectl delete service jobs-service-postgresql

kubectl delete sinkbinding jobs-service-postgresql-sb

kubectl delete trigger jobs-service-postgresql-cancel-job-trigger

kubectl delete trigger jobs-service-postgresql-create-job-trigger