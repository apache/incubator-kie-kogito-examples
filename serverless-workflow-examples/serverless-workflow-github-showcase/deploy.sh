#!/bin/bash

# Build every project using docker
# configure the ports as:
#   - flow:8080
#   - aggregator: 8181
#   - credit-bureau: 8282
#   - ui: 8383
#   - bank1: 8484 (we will have only one bank since we can't broadcast the message locally)
# the connectivity between the images works like this:
#  flow -> credit-bureau -> flow -> bank1 -> aggregator -> flow -> aggregator -> flow -> ui

# every image must name ko.local/loanbroker-<servicename>

# build the flow
# cd loanbroker-flow
# mvn clean install -DskipTests
# docker run --rm -it -p 8080:8080 -e K_SINK=http://localhost:8383 dev.local/loanbroker-aggregator

# build credit bureau
# no need of any additional env variable or setup
# cd credit-bureau
# kn func build --image dev.local/loanbroker-credit-bureau
# docker run --rm -it -p 8181:8080 dev.local/loanbroker-credit-bureau

# build the aggregator
# cd aggregator
# mvn clean install -DskipTests
# docker run --rm -it -p 8282:8080 -e K_SINK=http://localhost:8080 dev.local/loanbroker-aggregator

# build the UI
# cd loanbroker-ui
# mvn clean install -DskipTests
# docker run --rm -it -p 8383:8080 dev.local/loanbroker-ui

# build the banks
# cd banks
# kn func build --image dev.local/loanbroker-bank
# docker run --rm -it -p 8484:8080 --env-file=bank1.env dev.local/loanbroker-bank
# docker run --rm -it -p 8585:8080 --env-file=bank2.env dev.local/loanbroker-bank
# docker run --rm -it -p 8686:8080 --env-file=bank3.env dev.local/loanbroker-bank

SKIP_BUILD=$1
DEPLOY_LOG=deploy.log
# remember to change in kubernetes.yml
NAMESPACE=github-showcase

print_build_header() {
    PROJ=$1
    echo -e "*********** IMAGE BUILD LOG $PROJ ***********\n" >> ../$DEPLOY_LOG
    echo "Building image for project $PROJ"
}

print_build_footer() {
    PROJ=$1
    RETURN_CODE=$2
    if [ $RETURN_CODE -gt 0 ]
    then 
        echo "Image build for $PROJ failed" >&2
        exit 1
    fi
    echo -e "\n" >> ../$DEPLOY_LOG
}

apply_kube() {
    YAML_FILE=$1
    NAME=$2
    echo "*********** CREATING $NAME k8s OBJECTS ***********\n" >> ../$DEPLOY_LOG
    echo "Creating k8s $NAME"
    kubectl apply -n $NAMESPACE -f $YAML_FILE >> $DEPLOY_LOG
    RETURN_CODE=$?
    if [ "${RETURN_CODE}" -gt 0 ]
    then 
        echo "Failed to create $NAME objects" >&2
        exit 1
    fi
    echo -e "\n" >> $DEPLOY_LOG
}
expose_pr_checker_workflow() {
    echo "Exposing flow, please run 'minikube tunnel -p knative' in a separate terminal"
    DEPLOYMENT=$(kubectl get deployment --selector=serving.knative.dev/service=pr-checker-flow -n github-showcase | awk 'NR==2{print $1}')
    kubectl expose deployment $DEPLOYMENT --name=pr-checker-flow-external --type=LoadBalancer --port=8080 -n ${NAMESPACE}
}

expose_github_service() {
    echo "Exposing flow, please run 'minikube tunnel -p knative' in a separate terminal"
    DEPLOYMENT=$(kubectl get deployment --selector=serving.knative.dev/service=github-service -n github-showcase | awk 'NR==2{print $1}')
    kubectl expose deployment $DEPLOYMENT --name=github-service-external --type=LoadBalancer --port=8080 -n ${NAMESPACE}
}

expose_notification_service() {
    echo "Exposing flow, please run 'minikube tunnel -p knative' in a separate terminal"
    DEPLOYMENT=$(kubectl get deployment --selector=serving.knative.dev/service=notification-service -n github-showcase | awk 'NR==2{print $1}')
    kubectl expose deployment $DEPLOYMENT --name=notification-service-external --type=LoadBalancer --port=8080 -n ${NAMESPACE}
}

rm -rf $DEPLOY_LOG

if [ "$SKIP_BUILD" != true ]
then
    echo "Setting Docker Env to Minikube"
    eval $(minikube -p minikube docker-env --profile knative)
    if [ $? -gt 0 ]
    then 
        echo "Failed to set docker-env to minikube" >&2
        exit 1
    fi
    echo "mvn -B clean install -Dquarkus.kubernetes.namespace=$NAMESPACE -DskipTests -Pknative >> ../$DEPLOY_LOG"
fi

apply_kube "kubernetes/github-showcase-database.yml" "Database and Namespace"
apply_kube "kubernetes/jobs-service-postgresql.yml" "Kogito Jobs Service"
apply_kube "kubernetes/kubernetes.yml" "Event Display and triggers"
apply_kube "kubernetes/github-webhook-secret.yml" "Github webhook secret"
apply_kube "kubernetes/github-source.yml" "GithubSource"
apply_kube "github-service/target/kubernetes/knative.yml" "Github Service"
apply_kube "notification-service/target/kubernetes/knative.yml" "Notification Service"
apply_kube "pr-checker-workflow/target/kubernetes/knative.yml" "Flow Kogito Binding"
apply_kube "pr-checker-workflow/target/kubernetes/kogito.yml" "Flow Service"

expose_pr_checker_workflow
expose_github_service
expose_notification_service
