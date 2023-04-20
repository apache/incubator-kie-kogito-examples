/*
* This file is describing all the Jenkins jobs in the DSL format (see https://plugins.jenkins.io/job-dsl/)
* needed by the Kogito pipelines.
*
* The main part of Jenkins job generation is defined into the https://github.com/kiegroup/kogito-pipelines repository.
*
* This file is making use of shared libraries defined in
* https://github.com/kiegroup/kogito-pipelines/tree/main/dsl/seed/src/main/groovy/org/kie/jenkins/jobdsl.
*/

import org.kie.jenkins.jobdsl.model.JenkinsFolder
import org.kie.jenkins.jobdsl.model.JobType
import org.kie.jenkins.jobdsl.utils.EnvUtils
import org.kie.jenkins.jobdsl.utils.JobParamsUtils
import org.kie.jenkins.jobdsl.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoJobUtils
import org.kie.jenkins.jobdsl.Utils

jenkins_path = '.ci/jenkins'

Map getMultijobPRConfig(JenkinsFolder jobFolder) {
    def jobConfig = [
        parallel: true,
        buildchain: true,
        jobs : [
            [
                id: 'kogito-quarkus-examples',
                primary: true,
                env : [
                    // Sonarcloud analysis is disabled for examples
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'kogito-quarkus-examples/',
                    BUILD_MVN_OPTS_CURRENT: jobFolder.getEnvironmentName() ? '' : '-Dvalidate-formatting', // Validate formatting only for default env
                ]
            ],
            [
                id: 'kogito-springboot-examples',
                primary: true,
                env : [
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'kogito-springboot-examples/',
                ]
            ],
            [
                id: 'serverless-workflow-examples',
                primary: true,
                env : [
                    KOGITO_EXAMPLES_SUBFOLDER_POM: 'serverless-workflow-examples/',
                ]
            ]
        ]
    ]

    // TODO to remove once quarkus-3 env is stable
    if (EnvUtils.hasEnvironmentId(this, jobFolder.getEnvironmentName(), 'quarkus3')) {
        jobConfig.jobs.clear()
    }

    return jobConfig
}

// PR checks
KogitoJobUtils.createAllEnvironmentsPerRepoPRJobs(this) { jobFolder -> getMultijobPRConfig(jobFolder) }
setupDeployJob(JobType.PULL_REQUEST, 'kogito-bdd')

// Init branch
createSetupBranchJob()

// Nightly jobs
KogitoJobUtils.createNightlyBuildChainBuildAndDeployJobForCurrentRepo(this, '', true)

// Environment nightlies
setupSpecificBuildChainNightlyJob('native')

// Jobs with integration branch
setupNightlyQuarkusIntegrationJob('quarkus-main')
setupNightlyQuarkusIntegrationJob('quarkus-branch')
setupNightlyQuarkusIntegrationJob('quarkus-lts')
setupNightlyQuarkusIntegrationJob('native-lts')
// setupNightlyQuarkusIntegrationJob('quarkus-3')

// Release jobs
setupDeployJob(JobType.RELEASE)
setupPromoteJob(JobType.RELEASE)
setupPostReleaseJob()

KogitoJobUtils.createQuarkusUpdateToolsJob(this, 'kogito-examples', [
  properties: [ 'quarkus-plugin.version', 'quarkus.platform.version' ],
])

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupNightlyQuarkusIntegrationJob(String envName) {
    KogitoJobUtils.createNightlyBuildChainIntegrationJob(this, envName, Utils.getRepoName(this), true)
}

void setupSpecificBuildChainNightlyJob(String envName) {
    KogitoJobUtils.createNightlyBuildChainBuildAndTestJobForCurrentRepo(this, envName, true)
}

void createSetupBranchJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-examples', JobType.SETUP_BRANCH, "${jenkins_path}/Jenkinsfile.setup-branch", 'Kogito Examples Init branch')
    JobParamsUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.env.putAll([
        REPO_NAME: 'kogito-examples',
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",

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
void setupDeployJob(JobType jobType, String envName = '') {
    def jobParams = JobParamsUtils.getBasicJobParamsWithEnv(this, 'kogito-examples-deploy', jobType, envName, "${jenkins_path}/Jenkinsfile.deploy", 'Kogito Examples Deploy')
    JobParamsUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    if (jobType == JobType.PULL_REQUEST) {
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
    if (jobType == JobType.PULL_REQUEST) {
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
        if (jobType == JobType.RELEASE) {
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
            if (jobType == JobType.PULL_REQUEST) {
                // author can be changed as param only for PR behavior, due to source branch/target, else it is considered as an env
                stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
            }

            booleanParam('SKIP_TESTS', false, 'Skip tests')

            booleanParam('CREATE_PR', false, 'Should we create a PR with the changes ?')

            stringParam('PROJECT_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('DROOLS_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupPromoteJob(JobType jobType) {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-examples-promote', jobType, "${jenkins_path}/Jenkinsfile.promote", 'Kogito Examples Promote')
    JobParamsUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
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

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupPostReleaseJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-examples-post-release', JobType.RELEASE, "${jenkins_path}/Jenkinsfile.post-release", 'Kogito Examples Post Release')
    JobParamsUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.env.putAll([
        REPO_NAME: 'kogito-examples',
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GITHUB_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            stringParam('PROJECT_VERSION', '', 'Project version.')
            stringParam('GIT_TAG', '', '(Optional) Git tag to use. Default is the project version')

            booleanParam('SEND_NOTIFICATION', true, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}