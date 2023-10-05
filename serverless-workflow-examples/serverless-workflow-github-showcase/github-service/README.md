## GitHub API Wrapper Service

This service's responsibility is to manage GitHub API calls and to provide authentication
tokens every 5 minutes to make valid calls to the App API. It exposes three REST functions to interact with
the GitHub API. The table below lists the provided endpoints:

| Function | Endpoint | Description |
|----------|----------|-------------|
| Add Labels        | POST `/repo/{user}/{repository}/pr/{id}/labels`    | Adds a list of labels to the given Pull Request |
| Add Reviewers     | POST `/repo/{user}/{repository}/pr/{id}/reviewers` | Adds a list of reviewers the the given Pull Request |
| Get changed files | GET `/repo/{user}/{repository}/pr/{id}/files`      | Fetches for the changed files in a given Pull Request |

### Configuring your GitHub App

For this service to work, you will need to create a new GitHub App and install it
in one or more repositories to be able to make changes to its PRs or Issues.

By creating a GitHub App, it will provide a private key which can be used by this
service to interact with the GitHub API.

#### Creating a new GitHub App

[Follow the GitHub documentation](https://docs.github.com/en/developers/apps/setting-up-your-development-environment-to-create-a-github-app) to create a new GitHub App and 
a private key.

When asked to set permissions, choose "Access: Read & Write" for **Issues** and **Pull Requests**. Then, in "Subscribe to Events" section set "Pull request".
This way you will be able to make changes in the repositories' PRs where you've installed the GitHub App.

After creating the private key, download it locally in somewhere safe. 

#### Converting the generated private key 

Convert the downloaded private key from PEM to DER format with the following command:
 
```shell-script
openssl pkcs8 -topk8 -inform PEM -outform DER -in ~/github-api-app.private-key.pem -out ~/github-api-app.private-key.der -nocrypt
```

Replace the file `~/github-api-app.private-key.pem` with your local path.

This will generate a private key in DER format which we will use to generate the GitHub
API tokens on demand.

#### Installing the App in one or more repositories

It's recommended to install the GitHub App in a test repository to not mess with 
your account or organizations.

Create a new repository, then go to your [Developer Settings](https://github.com/settings/apps),
click on "Edit" button next to your GitHub App, then click on "Install App", choose your account and install it in the test repository you just created.

### Trying the service locally

Now you have the GitHub App, a test repository and a private key. It's time to try
the application locally.

Clone this repo if you haven't yet, edit the file [`src/main/resources/application.properties`](src/main/resources/application.properties)
and add the following data:

```properties
org.acme.examples.sw.github.service.key=<LOCAL PATH TO YOUR DER FILE>
org.acme.examples.sw.github.service.installation_id=<APP INSTALLATION ID>
org.acme.examples.sw.github.service.app_id=<APP ID>
``` 

Replace `<LOCAL PATH TO YOUR DER FILE>` with the absolute path of the converted private key file (DER format).

`<APP INSTALLATION ID>` can be grabbed in the [Installations Dashboard](https://github.com/settings/installations/).
Just click in "Configure" button next to the app name, and you will be redirected to the Installation page. 
The installation id is in the end of the URL, e.g.: `https://github.com/settings/installations/12345`.

The `<APP_ID>` is the number displayed in the App Dashboard. 
Click in the "App settings" link to be redirected to the dashboard.

Having everything in place, start the Quarkus application with the following command:

```shell script
mvn clean quarkus:dev
```

Then access the Swagger UI to play around with the API: http://localhost:8080/swagger-ui

:warning: **Important**:

1. Open a test PR to have some data to play with
2. Invite a friend to be a contributor to your repo, so you can make the service request for their review in the PRs :kissing:

### Deploying on Kubernetes

> **IMPORTANT! :warning:** we assume you have read the prerequisites section in the main
> [README file](../README.md). Please follow those instructions before continuing.

**Heads up!** This service will be deployed as a Knative Service instead of a regular Kubernetes
Deployment.

To make things easier there is a [script in this directory](deploy-kubernetes.sh) to generate the template
files, build the application and the image, and then deploy it to your Kubernetes cluster. 

**IMPORTANT!** You **must** be authenticated to the target Kubernetes cluster as a **cluster administrator** for this script
to work.

You can run the script once and all the required files will be generated in the `kubernetes` directory, 
and the image will be published to your Quay.io account.

Fill the value for the variables as shown below and run the script:

```shell script
# the script accepts positional arguments as following:
QUAY_NAMESPACE=
APP_ID=
INSTALLATION_ID=
DER_FILE=

./deploy-kubernetes.sh $QUAY_NAMESPACE $APP_ID $INSTALLATION_ID $DER_FILE
```

You should see a similar output like this:

<details><summary>Build logs</summary>
```
// build logs surpressed
---> Building and pushing image using tag quay.io/ricardozanini/github-service:latest
STEP 1: FROM adoptopenjdk:11-jre-hotspot
STEP 2: RUN mkdir -p /opt/app/lib
--> Using cache 26183c5ad8a51a030030a250db0c99e649fdd9668ef4766d0b66782d0dad7573
STEP 3: COPY target/github-service-2.0.0-SNAPSHOT-runner.jar /opt/app
--> 31bc2627d32
STEP 4: COPY target/lib/*.jar /opt/app/lib
--> 62eae5cdde7
STEP 5: CMD ["java", "-jar", "/opt/app/github-service-2.0.0-SNAPSHOT-runner.jar"]
STEP 6: COMMIT quay.io/ricardozanini/github-service:latest
--> 7c555a3060c
7c555a3060c666582824552d8824f2787b59b67b506fb933b171764bde894730
Getting image source signatures
Copying config 7c555a3060 [--------------------------------------] 0.0b / 6.2KiB
Writing manifest to image destination
Writing manifest to image destination
Storing signatures
---> Applying objects to the cluster in the namespace kogito-github.
configmap/github-service-properties unchanged
secret/github-app-ids unchanged
secret/github-app-key unchanged
service.serving.knative.dev/github-service configured
```
</details>

To verify if the service have been correctly deployed run:

```
$ kubectl get ksvc github-service  -n kogito-github

NAME             URL                                               LATESTCREATED          LATESTREADY            READY   REASON
github-service   http://github-service.kogito-github.example.com   github-service-7frvw   github-service-7frvw   True    
```

The `READY` column should be true.

#### Exposing the service on Minikube

If you're running on another cluster than Minikube, the service's route exposed by Knative Serving probably is accessible to you.
On Minikube there are some additional steps to be made. 

Run a new terminal window:

```shell script
minikube tunnel
```

Leave the process executing and then execute:

```shell script
./expose-on-minikube.sh
```

This script will fetch the Minikube IP exposed by the `tunnel` command and add the route to your local `/etc/hosts` file.

You can then access the service via the service URL:

```
$  kubectl get ksvc github-service  -n kogito-github --output jsonpath="{.status.url}"

http://github-service.kogito-github.example.com
```

As we did when running through the `jar` file, we can access the Swagger UI and play around with the API: 

http://github-service.kogito-github.example.com/swagger-ui

The first query may take a little time to return since Knative will start the service's pod on demand. 
After some time the pod will just terminate. 

Congratulations! The GitHub functions is now available in the cluster ready to be consumed by the Kogito Workflow.

### Cleaning up!

See the project root's [README](./README.md) documentation.