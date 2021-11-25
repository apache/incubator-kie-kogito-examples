#!/bin/sh

ns="process-usertasks-timer"

oc new-project ${ns}
oc project ${ns}

kubectl apply -f infinispan/infinispan.yaml
kubectl apply -f kafka/kafka.yaml

while [[ $(kubectl get pods -l app=infinispan-pod -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do echo "waiting for Infinispan pod" && sleep 1; done
while [[ $(kubectl get pods -l app.kubernetes.io/name=kafka -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do echo "waiting for Kafka pod" && sleep 1; done

kubectl apply -f kogito/kogito-infinispan-infra.yaml
kubectl apply -f kogito/kogito-kafka-infra.yaml

kubectl apply -f kogito/kogito-job-services.yaml

###### KogitoRuntime ###########

kubectl apply -f kogito/process-usertasks-timer.yaml
kubectl create configmap process-usertasks-timer-protobuf-files --from-file=../target/classes/META-INF/resources/persistence/protobuf/hiring.proto
kubectl apply -f kogito/kogito-process-usertask-timer-infra.yaml

kubectl apply -f kogito/kogito-data-index.yaml
kubectl apply -f kogito/kogito-mgmt-console.yaml
kubectl apply -f kogito/kogito-task-console.yaml

################# Setup Keycloak ###############################
mgmt_console_url=$(kubectl get routes.route.openshift.io kogito-mgmt-console -o jsonpath='{.spec.host}')
sed -i "s|http://kogito-mgmt-console/|http://${mgmt_console_url}/|g" keycloak/kogito-realm.json

task_console_url=$(kubectl get routes.route.openshift.io kogito-task-console -o jsonpath='{.spec.host}')
sed -i "s|http://kogito-task-console/|http://${task_console_url}/|g" keycloak/kogito-realm.json

kubectl create configmap keycloak-realm-config --from-file=keycloak/kogito-realm.json
kubectl apply -f keycloak/keycloak.yaml
while [[ $(kubectl get pods -l app=kogito-keycloak -o 'jsonpath={..status.conditions[?(@.type=="Ready")].status}') != "True" ]]; do echo "waiting for Keycloak pod" && sleep 1; done

################# Append keycloak url into mgmt_console/task_console ###############################
keycloak_url=$(kubectl get routes.route.openshift.io kogito-keycloak -o jsonpath='{.spec.host}')
sed -i "s|http://kogito-keycloak/|http://${keycloak_url}/|g" kogito/kogito-mgmt-console.yaml
sed -i "s|http://kogito-keycloak/|http://${keycloak_url}/|g" kogito/kogito-task-console.yaml
kubectl apply -f kogito/kogito-mgmt-console.yaml
kubectl apply -f kogito/kogito-task-console.yaml
