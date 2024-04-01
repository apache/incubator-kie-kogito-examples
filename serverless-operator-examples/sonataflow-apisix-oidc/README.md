# SonataFlow Authentication and Authorization with Keycloak and APISIX

## Installing Keycloak

Install Keycloak with PostgreSQL:

```shell
kubectl create ns keycloak
kubectl kustomize manifests/bases | kubectl apply -f - -n keycloak
```

### Exposing Keycloak

```shell
# Let's use kubectl port-forward to equalize the Keycloak endpoint URI so APISIX and your local env access Keycloak using the same URL
# This method works even in Windows/Darwin where Podman/Docker won't give access to the internal network
# Hence, we must rely on tunnel/port-forward
kubectl port-forward $(kubectl get pods -l app=myapp --output=jsonpath='{.items[*].metadata.name}') 8080:8080 -n keycloak
```

Edit your `/etc/hosts` file to access Keycloak using the same DNS name:

```shell
sudo vi /etc/hosts
```

Then add this line to the file:

```127.0.0.1 keycloak.keycloak.svc.cluster.local```.

Now, you can access the Keycloak application via [http://keycloak.keycloak.svc.cluster.local:8080](http://keycloak.keycloak.svc.cluster.local:8080)

This is the same endpoint we will configure the APISIX Route. This is an important step since the token must be generated and validated by the same OIDC server and the host is part of the token validation.

### Authenticating

Using the above URL, you can try requesting a token using [Grant Type Password](https://www.keycloak.org/docs/23.0.7/securing_apps/#_resource_owner_password_credentials_flow).

```shell
curl \
  -d "client_id=apisix-ingress" \
  -d "client_secret=kDb8jS1asUOxYjDJb3bUaimzUen9PRD4" \
  -d "username=zanini" \
  -d "password=zanini" \
  -d "grant_type=password" \
  "http://keycloak.keycloak.svc.cluster.local:8080/realms/sonataflow/protocol/openid-connect/token
```

## Installing APISIX

```shell
ADMIN_API_VERSION=v3

helm install apisix apisix/apisix \                               
  --set service.type=NodePort \
  --set ingress-controller.enabled=true \
  --create-namespace \
  --namespace ingress-apisix \
  --set ingress-controller.config.apisix.serviceNamespace=ingress-apisix \
  --set ingress-controller.config.apisix.adminAPIVersion=$ADMIN_API_VERSION

kubectl get service --namespace ingress-apisix
```

### Exposing APISIX Ingress on Minikube

```shell
minikube service apisix-gateway --url -n ingress-apisix
```

## Installing SonataFlow Operator

Enable minikube registry:

```shell
minikube addons enable registry
```

Install the operator:

```shell
kubectl create -f https://raw.githubusercontent.com/apache/incubator-kie-kogito-serverless-operator/main/operator.yaml
```

### Deploy greeting workflow

```shell
kubectl create ns sonataflow
kubectl apply -f manifests/workflow-app/sonataflow-greeting.yaml -n sonataflow
```

### Add the APISIX Route to protect the workflow application

```shell
kubectl apply -f manifests/workflow-app/sonataflow-route.yaml -n sonataflow
```

## Validating if the setup works

First, try acquiring the token:

```shell
ACCESS_TOKEN=$(curl \
  -d "client_id=apisix-ingress" \
  -d "client_secret=<CLIENT_SECRET>" \
  -d "username=<USER_NAME>" \
  -d "password=<USER_PASSWORD>" \
  -d "grant_type=password" \
  "http://keycloak.keycloak.svc.cluster.local:8080/realms/sonataflow/protocol/openid-connect/token" | jq -r .access_token)
```

Then make a call to the SonataFlow Greeting workflow:

```shell
curl -v POST http://127.0.0.1:63031/greeting -H "Content-type: application/json" -H "Host: local.greeting.sonataflow.org" -H "Authorization: Bearer ${ACCESS_TOKEN}" --data '{ "name": "Zanini" }' 
```

This request is passing through the APISIX Gateway, which is validating the token via the `Authorization: Bearer` header. Then the request is passed internally to the workflow application, which will process and return to the original client.

## References

- [Keycloak - Getting Started with Kubernetes](https://www.keycloak.org/getting-started/getting-started-kube)
- [APISIX - authz-keycloak Plugin](https://apisix.apache.org/docs/apisix/plugins/authz-keycloak/)
- [APISIX Ingress - ApisixRoute Reference](https://apisix.apache.org/docs/ingress-controller/concepts/apisix_route/)
