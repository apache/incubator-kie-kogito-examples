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
#### PostgreSQL Config
- `kogito.persistence.type`
- `quarkus.datasource.db-kind`
- `quarkus.datasource.username`
- `quarkus.datasource.password`
- `quarkus.datasource.jdbc.url`
- `quarkus.datasource.reactive.url`
#### Kafka Config
- `kafka.bootstrap.servers` - the kafka url used by the Kogito application; can be internal service url
#### Kogito Dataindex Config
- `kogito.dataindex.props` - command line properties for the data index
- `kogito.dataindex.httpurl` - the dataindex url, protocol: http
- `kogito.dataindex.httpurl.with.graphql` - the dataindex graphql url
- `kogito.dataindex.wsurl` - the dataindex url, protocol: ws
#### Kogito Job Service Config
- `kogito.jobsservice.props` - command line properties for the job service 