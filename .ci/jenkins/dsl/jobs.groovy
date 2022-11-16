/*
* This file is describing all the Jenkins jobs in the DSL format (see https://plugins.jenkins.io/job-dsl/)
* needed by the Kogito pipelines.
*
* The main part of Jenkins job generation is defined into the https://github.com/kiegroup/kogito-pipelines repository.
*
* This file is making use of shared libraries defined in
* https://github.com/kiegroup/kogito-pipelines/tree/main/dsl/seed/src/main/groovy/org/kie/jenkins/jobdsl.
*/

import org.kie.jenkins.jobdsl.model.Folder
import org.kie.jenkins.jobdsl.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoJobUtils
import org.kie.jenkins.jobdsl.Utils

jenkins_path = '.ci/jenkins'

Map getMultijobPRConfig() {
    return [
        parallel: true,
        buildchain: true,
        jobs : [
            [
                id: 'kogito-quarkus-examples',
                primary: true,
                env : [
                    // Sonarcloud analysis is disabled for examples
                    DISABLE_SONARCLOUD: true,
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'kogito-quarkus-examples/',
                ]
            ],
            [
                id: 'kogito-springboot-examples',
                primary: true,
                env : [
                    // Sonarcloud analysis is disabled for examples
                    DISABLE_SONARCLOUD: true,
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'kogito-springboot-examples/',
                ]
            ],
            [
                id: 'serverless-workflow-examples',
                primary: true,
                env : [
                    // Sonarcloud analysis is disabled for examples
                    DISABLE_SONARCLOUD: true,
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'serverless-workflow-examples/',
                ]
            ]
        ]
    ]
}

// PR checks
KogitoJobUtils.createAllEnvsPerRepoPRJobs(this) { jobFolder -> getMultijobPRConfig() }
setupDeployJob(Folder.PULLREQUEST_RUNTIMES_BDD)

// Init branch
createSetupBranchJob()

// Nightly jobs
setupDeployJob(Folder.NIGHTLY)
setupSpecificBuildChainNightlyJob(Folder.NIGHTLY_NATIVE)

setupSpecificBuildChainNightlyJob(Folder.NIGHTLY_QUARKUS_MAIN)
setupSpecificBuildChainNightlyJob(Folder.NIGHTLY_QUARKUS_BRANCH)

setupSpecificBuildChainNightlyJob(Folder.NIGHTLY_MANDREL)
setupSpecificBuildChainNightlyJob(Folder.NIGHTLY_MANDREL_LTS)
setupSpecificBuildChainNightlyJob(Folder.NIGHTLY_QUARKUS_LTS)

// Jobs with integration branch
setupSpecificNightlyJob(Folder.NIGHTLY_QUARKUS_MAIN, true)
setupSpecificNightlyJob(Folder.NIGHTLY_QUARKUS_LTS, true)

// Release jobs
setupDeployJob(Folder.RELEASE)
setupPromoteJob(Folder.RELEASE)

KogitoJobUtils.createQuarkusUpdateToolsJob(this, 'kogito-examples', [
  properties: [ 'quarkus-plugin.version', 'quarkus.platform.version' ],
])

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupSpecificNightlyJob(Folder specificNightlyFolder, boolean useIntegrationBranch = false) {
    String envName = specificNightlyFolder.environment.toName()
    def jobParams = KogitoJobUtils.getBasicJobParams(this, "kogito-examples${useIntegrationBranch ? '-integration-branch' : ''}", specificNightlyFolder, "${jenkins_path}/Jenkinsfile.specific_nightly", "Kogito Examples Nightly ${envName}")
    KogitoJobUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.triggers = [ cron : '@midnight' ]
    jobParams.env.putAll([
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        NOTIFICATION_JOB_NAME: "${envName} check",
        USE_INTEGRATION_BRANCH : useIntegrationBranch,
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
            stringParam('GIT_AUTHOR_CREDS_ID', "${GIT_AUTHOR_CREDENTIALS_ID}", 'Set the Git author creds id')
        }
    }
}

void setupSpecificBuildChainNightlyJob(Folder specificNightlyFolder) {
    String envName = specificNightlyFolder.environment.toName()
    KogitoJobUtils.createNightlyBuildChainBuildAndTestJobForCurrentRepo(this, specificNightlyFolder, true, envName)
}

void createSetupBranchJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'kogito-examples', Folder.SETUP_BRANCH, "${jenkins_path}/Jenkinsfile.setup-branch", 'Kogito Examples Init branch')
    KogitoJobUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.env.putAll([
        REPO_NAME: 'kogito-examples',
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",

        IS_MAIN_BRANCH: "${Utils.isMainBranch(this)}"
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            stringParam('KOGITO_VERSION', '', 'Kogito version to set.')
            stringParam('DROOLS_VERSION', '', 'Drools version to set.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

/*
* Setup deploy job
* when using `isForPr` property, then git branch/author information are parameters instead of env
* as we want in that case more dynamic git retrieval
* also we set a specific repository for the pr checks
*/
void setupDeployJob(Folder jobFolder) {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'kogito-examples-deploy', jobFolder, "${jenkins_path}/Jenkinsfile.deploy", 'Kogito Examples Deploy')
    KogitoJobUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    if (jobFolder.isPullRequest()) {
        jobParams.git.branch = '${BUILD_BRANCH_NAME}'
        jobParams.git.author = '${GIT_AUTHOR}'
        jobParams.git.project_url = Utils.createProjectUrl("${GIT_AUTHOR_NAME}", jobParams.git.repository)
    }
    jobParams.env.putAll([
        REPO_NAME: 'kogito-examples',
        PROPERTIES_FILE_NAME: 'deployment.properties',

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
    ])
    if (jobFolder.isPullRequest()) {
        jobParams.env.putAll([
            MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_PR_CHECKS_REPOSITORY_URL}",
            MAVEN_DEPLOY_REPOSITORY: "${MAVEN_PR_CHECKS_REPOSITORY_URL}",
            MAVEN_REPO_CREDS_ID: "${MAVEN_PR_CHECKS_REPOSITORY_CREDS_ID}",
        ])
    } else {
        jobParams.env.putAll([
            GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

            AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
            GITHUB_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",
            GIT_AUTHOR_BOT: "${GIT_BOT_AUTHOR_NAME}",
            BOT_CREDENTIALS_ID: "${GIT_BOT_AUTHOR_CREDENTIALS_ID}",

            MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
            MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        ])
        if (jobFolder.isRelease()) {
            jobParams.env.putAll([
                NEXUS_RELEASE_URL: "${MAVEN_NEXUS_RELEASE_URL}",
                NEXUS_RELEASE_REPOSITORY_ID: "${MAVEN_NEXUS_RELEASE_REPOSITORY}",
                NEXUS_STAGING_PROFILE_ID: "${MAVEN_NEXUS_STAGING_PROFILE_ID}",
                NEXUS_BUILD_PROMOTION_PROFILE_ID: "${MAVEN_NEXUS_BUILD_PROMOTION_PROFILE_ID}",
            ])
        }
    }
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            if (jobFolder.isPullRequest()) {
                // author can be changed as param only for PR behavior, due to source branch/target, else it is considered as an env
                stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
            }

            booleanParam('SKIP_TESTS', false, 'Skip tests')

            booleanParam('CREATE_PR', false, 'Should we create a PR with the changes ?')
            booleanParam('UPDATE_NIGHTLY_BRANCH', false, 'Set to true if at the end of the run, the nightly branch should be updated. This CANNOT be used with `CREATE_PR` parameter also enabled (this latter one has priority). It is also disabled for release job.')

            stringParam('PROJECT_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('DROOLS_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupPromoteJob(Folder jobFolder) {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'kogito-examples-promote', jobFolder, "${jenkins_path}/Jenkinsfile.promote", 'Kogito Examples Promote')
    KogitoJobUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.env.putAll([
        REPO_NAME: 'kogito-examples',
        PROPERTIES_FILE_NAME: 'deployment.properties',

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GITHUB_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",
        GIT_AUTHOR_BOT: "${GIT_BOT_AUTHOR_NAME}",
        BOT_CREDENTIALS_ID: "${GIT_BOT_AUTHOR_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            // Deploy job url to retrieve deployment.properties
            stringParam('DEPLOY_BUILD_URL', '', 'URL to jenkins deploy build to retrieve the `deployment.properties` file. If base parameters are defined, they will override the `deployment.properties` information')

            // Release information which can override `deployment.properties`
            stringParam('PROJECT_VERSION', '', 'Override `deployment.properties`. Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('DROOLS_VERSION', '', 'Override `deployment.properties`. Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('GIT_TAG', '', 'Git tag to set, if different from PROJECT_VERSION')
            booleanParam('UPDATE_STABLE_BRANCH', false, 'Set to true if you want to update the `stable` branch to new created Git tag.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}
