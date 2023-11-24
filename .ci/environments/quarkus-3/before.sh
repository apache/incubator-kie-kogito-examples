#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

set -euo pipefail

script_dir_path=$(cd `dirname "${BASH_SOURCE[0]}"`; pwd -P)
mvn_cmd="mvn ${BUILD_MVN_OPTS:-} ${BUILD_MVN_OPTS_QUARKUS_UPDATE:-}"
ci="${CI:-false}"

rewrite_plugin_version=4.43.0
quarkus_version=${QUARKUS_VERSION:-3.5.2}

quarkus_recipe_file="${script_dir_path}/quarkus3.yml"
patch_file="${script_dir_path}"/patches/0001_before_sh.patch

if [ "${ci}" = "true" ]; then
    # In CI we need the main branch snapshot artifacts deployed locally
    set -x
    ${mvn_cmd} clean install -DskipTests
    set +x
fi

rewrite=${1:-'none'}
behavior=${2:-'none'}
echo "rewrite "${rewrite}
if [ "rewrite" != ${rewrite} ]; then
    echo "No rewrite to be done. Exited"
    exit 0
fi

export MAVEN_OPTS="-Xmx16192m"

echo "Update project with Quarkus version ${quarkus_version}"

set -x

# Retrieve Project & Drools version used
project_version=$(mvn -q -Dexpression=project.version -DforceStdout help:evaluate)
drools_version=$(mvn -q -pl :decisiontable-quarkus-example -Dexpression=version.org.drools -DforceStdout help:evaluate)
# New drools version is based on current drools version and increment the Major => (M+1).m.y
new_project_version=$(echo ${project_version} | awk -F. -v OFS=. '{$1 += 1 ; print}')
new_drools_version=$(echo ${drools_version} | awk -F. -v OFS=. '{$1 += 1 ; print}')

# Regenerate quarkus3 recipe
cd ${script_dir_path}
curl -Ls https://sh.jbang.dev | \
    bash -s - jbang/CreateKieQuarkusProjectMigrationRecipe.java \
        -v quarkus-plugin.version=${quarkus_version} \
        -v quarkus.platform.version=${quarkus_version} \
        -v version.org.drools=${new_drools_version} \
        -v version.org.kie.kogito=${new_project_version} \
        -v kogito.bom.version=${new_project_version}
cd -

# Launch Quarkus 3 Openrewrite
${mvn_cmd} org.openrewrite.maven:rewrite-maven-plugin:${rewrite_plugin_version}:run \
    -Drewrite.configLocation="${quarkus_recipe_file}" \
    -DactiveRecipes=io.quarkus.openrewrite.Quarkus \
    -Drewrite.recipeArtifactCoordinates=org.kie:jpmml-migration-recipe:"${drools_version}" \
    -Denforcer.skip \
    -fae \
    -Dexclusions=**/target \
    -DplainTextMasks=**/kmodule.xml

# Update dependencies with Quarkus 3 bom
${mvn_cmd} \
    -DremotePom=io.quarkus:quarkus-bom:${quarkus_version} \
    -DupdatePropertyVersions=true \
    -DupdateDependencies=true \
    -DgenerateBackupPoms=false \
    versions:compare-dependencies

# Create the `patches/0001_before_sh.patch` file
git add .
git reset "${quarkus_recipe_file}" # Do not include recipe file
git diff --cached > "${patch_file}"
git reset

# Commit the change on patch
if [ "$(git status --porcelain ${patch_file})" != '' ]; then
    if [ "$(git status --porcelain ${quarkus_recipe_file})" != '' ]; then
        git add "${quarkus_recipe_file}" # We suppose that if the recipe has changed, the patch file as well
    fi
    git add "${patch_file}"
    git commit -m '[Quarkus 3] Updated rewrite data'

    git reset --hard
    if [ "${behavior}" = 'push_changes' ]; then
        git_remote="${GIT_REMOTE:-origin}"
        branch=$(git branch --show-current)
        echo "Pushing changes to ${git_remote}/${branch} after rebase "
        git fetch ${git_remote}
        git rebase ${git_remote}/${branch}
        git push ${git_remote} ${branch}
    fi
fi

# Reset all other changes as they will be applied next by the `patches/0001_before_sh.patch` file
git reset --hard
