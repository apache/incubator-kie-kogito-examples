## Prepare Kogito application image

The Tryout process installs a container image, which must be accessible from an image repository. If such an image exists, this step can be skipped.
Below steps show the process of building and uploading an image using the example of the [extended Kogito Travel Agency](https://github.com/apache/incubator-kie-kogito-examples/tree/stable/kogito-quarkus-examples/kogito-travel-agency/extended/travels/) application:
- cd into `kogito-examples/kogito-quarkus-examples/kogito-travel-agency/extended/travels`
- build the application: `mvn clean package`
- build the image: `docker build -f src/main/docker/Dockerfile.jvm -t quarkus/kogito-travel-agency-travels-jvm .`
- log into a image repository e.g. quay.io: `podman login quay.io`
- tag the local image for your chosen remote repository:
  `docker tag $(docker images | grep quarkus/kogito-travel-agency-travels-jvm | awk '{printf $3}') quay.io/<your user>/kogito-travel-agency-travels-jvm:1.0.0`
- push the tagged image: `docker push quay.io/<your user>/kogito-travel-agency-travels-jvm:1.0.0`
