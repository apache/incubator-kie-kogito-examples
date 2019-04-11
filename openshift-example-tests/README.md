# How to execute test

The integration tests are bound to Failsafe plugin. They can be executed as part of the build by running `mvn clean install -Popenshift`.
Another way is to run the tests using IDE as usual JUnit test.

Tests are using XTF framework for communication with OpenShift (which is based on Fabric8)

To properly configure OpenShift and related images user needs to specify these properties:
* xtf.openshift.url
* xtf.openshift.admin.username - Username of user with admin rights
* xtf.openshift.admin.password
* xtf.openshift.master.username - Username for user without admin rights (can be used admin user from above)
* xtf.openshift.master.password
* xtf.openshift.binary.path - Path to OC binary client (test/framework will be refactored in future to get rid of this)
* image.kaas.quarkus.builder.s2i - Tag for S2I Quarkus image, for example built from https://github.com/kiegroup/submarine-cloud/tree/master/s2i/kaas-quarkus-centos-s2i
* image.kaas.quarkus.runtime - Tag for Quarkus runtime image, for example built from https://github.com/kiegroup/submarine-cloud/tree/master/s2i/kaas-quarkus-centos
* image.kaas.springboot.builder.s2i - Tag for S2I image, for example buil from https://github.com/kiegroup/submarine-cloud/tree/master/s2i/kaas-springboot-centos-s2i
* image.kaas.springboot.runtime - Tag for runtime image, for example built from https://github.com/kiegroup/submarine-cloud/tree/master/s2i/kaas-springboot-centos

These properties need to be set as system property or they can be placed in test.properties file (with appropriate values). test.properties file can be placed next to pom.xml of the parent directory. It is added to .gitignore.
