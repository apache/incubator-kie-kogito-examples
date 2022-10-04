#!/bin/bash

NAMESPACE=newsletter-showcase

# commands to delete the subscription-flow

kn service delete subscription-flow -n $NAMESPACE

kubectl delete serviceaccount subscription-flow -n $NAMESPACE

kubectl delete sinkbinding sb-subscription-flow -n $NAMESPACE

kubectl delete trigger confirm-subscription-trigger-subscription-flow -n $NAMESPACE

# commands to delete the subscription-service

kn service delete subscription-service -n $NAMESPACE