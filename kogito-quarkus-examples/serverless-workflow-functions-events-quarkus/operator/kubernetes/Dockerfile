FROM quay.io/kiegroup/kogito-runtime-jvm:latest

ENV RUNTIME_TYPE quarkus

# How to use this image with a simple example:
# 1. Clone kogito-examples repository
# 2. Build the serverless-workflow-functions-events-quarkus with `mvn clean package -DskipTests=true
# 3. Build the image: docker build --tag quay.io/<yournamespace>/patient-onboarding:latest -f operator/kubernetes/Dockerfile .
#   3.1 Optionally test the image locally: docker run --rm -it -p 8080:8080 quay.io/<yournamespace>/patient-onboarding:latest
# 4. Push it: docker push quay.io/<yournamespace>/patient-onboarding:latest
# 6. Deploy it on Kubernetes with the kogito operator using the patient-onboarding.yaml file included in this directory

COPY target/*-runner.jar $KOGITO_HOME/bin
COPY target/lib $KOGITO_HOME/bin/lib
