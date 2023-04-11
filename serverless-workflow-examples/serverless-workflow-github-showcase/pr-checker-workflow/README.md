## Pull Request Checker Workflow

Before going further, please make sure you have deployed the [GitHub](../github-service) and [Notification](../notification-service)
services since the workflow depends on them.

This is the main service of this example, it's responsible to orchestrate the services in
order to verify an opened PR. The service will add labels and reviewers according
to the files that have been modified.

The image below illustrates the workflow:

![](docs/pr-checker-workflow.png)

Once a new PR is opened or changed in a given GitHub repository, the Knative platform will generate
a new CloudEvent containing the pull request context. The Broker will deliver this 
event to the workflow service, thus starting a new instance.

When the workflow starts, it will call the GitHub service to fetch the files changed in the PR.
Then, the workflow will verify what kind of changes have been made in the PR: 
based on the files' path, a specific label and reviewer will be added to it.

![](docs/handle-backend.png)
![](docs/handle-frontend.png)

In the end of the workflow, a new event will be created and sent to the Knative broker.
This event will be consumed by the [Notification service](../notification-service) 
to post a new message to a given Slack channel.

### Review the sub-flows Labels and Reviewers

Open the files `src/main/resources/handle-backend.sw.json` and `src/main/resources/handle-frontend.sw.json`
to review the labels and reviewers you wish to add to your PRs. Modify the `Inject` state
and save the workflow files.

_**Note**: You can modify the workflow to call a function and set the labels and reviewers
in the context based on a configuration file instead. Are you up to the challenge?_

### Review GitHub App WebHook Secret

> **HEADS UP!** we assume you already have the GitHub App created for this demo when
> you deployed the GitHub service

Go to your [GitHub Apps dashboard](https://github.com/settings/apps) and click on "Edit" button
in the application you created when deploying the [GitHub Service](../github-service). 

Edit the field "Webhook secret (optional)" and add the value `super-kogito-demo-secret`
in the text field. This secret will be used by the Knative platform to identify the incoming events.

_**Note:** In the "Webhook" section you should have a link to the [smee.io](https://smee.io/) service 
if you chose to run the demo locally or in a cluster that is not accessible from the external world.
Just make sure that the Webhook link is correct._ 

### Install Knative GitHub Source

Install the GitHub source from [eventing-github](https://github.com/knative-sandbox/eventing-github/releases) releases that match your Knative Eventing
platform. 

```shell script
$ kubectl apply -f https://github.com/knative-sandbox/eventing-github/releases/download/k<VERSION>/github.yaml
```

**Note:** replace `<VERSION>` with the correct Knative Eventing version.


If your cluster is already ready to receive GitHub Webhooks calls, just create
a new PR in your repository with a file named "backend", and you should see the PR
being labeled as "backend", also your chosen friend will be notified to review the PR.

If you're running on Minikube locally, proceed to the next section before trying the demo.   

### Setting up your cluster to be publicly available

Skip this section if your cluster is already publicly available and capable
to receive events from GitHub Webhooks.

#### Minikube

If you reach this point, you probably have tested and deployed the other services. 
Just make sure you have a terminal window opened with:

```
$ minikube tunnel
```

Now run:

```
$ kubectl expose deployment pr-checker-flow --name=pr-checker-flow-external --type=LoadBalancer --port=8080 -n github-showcase
```
This will expose pr-checker-flow service to be accessed from outside of cluster.

Now on a new terminal window run:

```
$ SMEE_WEBHOOK=<YOUR_SMEE_WEBHOOK>
$ ROUTE=$( kubectl get route pr-checker-flow -o jsonpath='{.status.url}' -n github-showcase)

$ smee -u $SMEE_WEBHOOK -t $ROUTE
```

Replace `<YOUR_SMEE_WEBHOOK>` with the Smee URL generated for you while creating a new GitHub App.

The Smee CLI will capture all events coming from your repository and redirect
to your local cluster, you should see the Knative pods starting on demand and in the end
a message in the Slack channel. :)

### Cleaning up!

See the project root's [README](./README.md) documentation.
