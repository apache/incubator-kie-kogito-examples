#!/bin/sh

# commands sequence to remove the timeouts-showcase-database:

kubectl delete deployment timeouts-showcase-database

kubectl delete service timeouts-showcase-database

kubectl delete secret timeouts-showcase-database