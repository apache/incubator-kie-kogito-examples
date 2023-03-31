#!/bin/bash
set -euo pipefail

mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"
quarkus_version="${QUARKUS_VERSION}"

echo "Update project with Quarkus version ${QUARKUS_VERSION}"

set -x

# Update with Quarkus version and commit
${mvn_cmd} \
    -Dproperty=quarkus.platform.version \
    -DnewVersion=${quarkus_version} \
    -DgenerateBackupPoms=false \
    -Dmaven.wagon.http.ssl.insecure=true \
    versions:set-property

${mvn_cmd} \
    -Dproperty=quarkus-plugin.version \
    -DnewVersion=${quarkus_version} \
    -DgenerateBackupPoms=false \
    -Dmaven.wagon.http.ssl.insecure=true \
    versions:set-property
