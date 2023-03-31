## App-Infra Connector

This is the place to define all that is necessary to loosly couple the Kogito application with
any of the available infrastructure applications and services.

There is one config map per infrastructure component.

### Config Map per infrastructure
#### Keycloak Config
- `keycloak.admin.user` - the username used to connect to Keycloak administration console
- `keycloak.admin.password` - the administration consoles' users' password
- `keycloak.realm.json` - Keycloak initialization file for the Kogito realm creating clients, users, etc. used in Kogito examples
- `keycloak.db.vendor` - Keycloak persistence
#### Infinispan Config
- `quarkus.infinispan.client.hosts` - the infinispan url used by the Kogito application; can be internal service url
- `quarkus.infinispan.client.username` - the user used by the Kogito application to access the infinispan service
#### Kafka Config
- `kafka.bootstrap.servers` - the kafka url used by the Kogito application; can be internal service url
#### Kogito Dataindex Config
- `kogito.dataindex.props` - command line properties for the data index
- `kogito.dataindex.httpurl` - the dataindex url, protocol: http
- `kogito.dataindex.httpurl.with.graphql` - the dataindex graphql url
- `kogito.dataindex.wsurl` - the dataindex url, protocol: ws
#### Kogito Management Console Config
- `kogito.managementconsole.props` - command line properties for the management console
#### Kogito Task Console Config
- `kogito.taskconsole.props` - command line properties for the task console
#### Kogito Job Service Config
- `kogito.jobsservice.props` - command line properties for the job service 