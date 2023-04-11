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
org.kogito.examples.sw.github.service.key=<LOCAL PATH TO YOUR DER FILE>
org.kogito.examples.sw.github.service.installation_id=<APP INSTALLATION ID>
org.kogito.examples.sw.github.service.app_id=<APP ID>
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

### Running on knative

> **IMPORTANT! :warning:** we assume you have read the prerequisites section in the main
> [README file](../README.md). Please follow those instructions before continuing.

 Run `mvn clean install -Pknative`

 Deploy the service with the following command:

 ```shell
 # install the github-service 
 $ kubectl apply -f github-service/target/kubernetes/knative.yml -n github-showcase
 ```
To verify if the service have been correctly deployed run:

```
 $ kubectl get ksvc github-service  -n github-showcase
NAME                  URL                                                         LATESTCREATED               LATESTREADY      READY   REASON
github-service   http://github-service.github-showcase.10.104.64.247.sslip.io   github-service-00001     github-service-00001   True    

```
The `READY` column should be true.

#### Exposing the service on Minikube

Execute the following command to expose the knative service

Run a new terminal window:

```shell script
minikube tunnel
```

Leave the process executing and then execute:

```shell
 # expose the github-service 
 $ kubectl expose deployment github-service --name=github-service-external --type=LoadBalancer --port=8080 -n github-showcase
```

You can then access the service via the service URL:

```
$   kubectl get ksvc github-service  -n github-showcase --output jsonpath="{.status.url}"

http://github-service.github-showcase.10.104.64.247.sslip.io
```

As we did when running through the `jar` file, we can access the Swagger UI and play around with the API: 

http://github-service.github-showcase.10.104.64.247.sslip.io/q/swagger-ui

The first query may take a little time to return since Knative will start the service's pod on demand. 
After some time the pod will just terminate. 

Congratulations! The GitHub functions is now available in the cluster ready to be consumed by the Kogito Workflow.

### Cleaning up!

See the project root's [README](./README.md) documentation.
