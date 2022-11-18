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
# remember to change in kubernetes.yaml
NAMESPACE=loanbroker-example

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

build_maven_image() {
    PROJ=$1

    cd $PROJ
    print_build_header $PROJ
    mvn -B clean install -Dquarkus.kubernetes.namespace=$NAMESPACE -DskipTests >> ../$DEPLOY_LOG
    print_build_footer $PROJ $?
    cd - >> /dev/null
}

build_kn_image() {
    PROJ=$1
    IMAGE_NAME=$2

    cd $PROJ
    print_build_header $PROJ
    kn func build -v -n $NAMESPACE --image $IMAGE_NAME >> ../$DEPLOY_LOG
    print_build_footer $PROJ $?
    cd - >> /dev/null
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

add_flow_url_to_ui() {
    echo "Getting Loan Broker URL"
    LOAN_FLOW_URL=""
    
    while [ -z "${LOAN_FLOW_URL}" ]
    do
        LOAN_FLOW_URL=$(kn service describe loanbroker-flow  -o jsonpath --template="{.status.url}" -n ${NAMESPACE})
        sleep 3
    done
    
    echo "Loan Broker URL is ${LOAN_FLOW_URL}"
    echo "Adding Flow URL to UI"

    DEFAULT_URL="http://loanbroker-flow.loanbroker-example.svc.cluster.local"
    sed -i .bak 's,'"${DEFAULT_URL}"','"${LOAN_FLOW_URL}"',g' loanbroker-ui/target/kubernetes/kubernetes.yml
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
    build_maven_image "loanbroker-ui"
    build_maven_image "loanbroker-flow"
    build_maven_image "aggregator"
    build_kn_image "credit-bureau" "dev.local/loanbroker-credit-bureau"
    build_kn_image "banks" "dev.local/loanbroker-bank"
fi

apply_kube "kubernetes.yaml" "Banks, Credit Bureau and Namespace"
apply_kube "loanbroker-flow/target/kubernetes/kogito.yml" "Flow Kogito Binding"
apply_kube "loanbroker-flow/target/kubernetes/knative.yml" "Flow Service"
apply_kube "aggregator/target/kubernetes/kubernetes.yml" "Aggregator Service"
# get the flow ksvc address to set to the yaml file before applying
add_flow_url_to_ui
apply_kube "loanbroker-ui/target/kubernetes/kubernetes.yml" "User Interface"
echo "Exposing UI at localhost:8080, please run 'minikube tunnel -p knative' in a separate terminal"
kubectl expose deployment loanbroker-ui --name=loanbroker-ui-external --type=LoadBalancer --port=8080 -n ${NAMESPACE}
