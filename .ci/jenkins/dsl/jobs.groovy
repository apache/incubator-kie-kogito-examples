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

boolean isMainStream() {
    return Utils.getStream(this) == 'main'
}

Map getMultijobPRConfig(JenkinsFolder jobFolder) {
    String defaultBuildMvnOptsCurrent = jobFolder.getDefaultEnvVarValue('BUILD_MVN_OPTS_CURRENT') ?: ''
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
                    BUILD_MVN_OPTS_CURRENT: "${defaultBuildMvnOptsCurrent} ${getExamplesBuildMvnOptions(jobFolder).join(' ')}",
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

    return jobConfig
}

List getExamplesBuildMvnOptions(JenkinsFolder jobFolder) {
    List mvnOpts = []
    if (isMainStream() && !jobFolder.getEnvironmentName()) {
        // Validate formatting only for default env
        mvnOpts += ['-Dvalidate-formatting']
    }
    return mvnOpts
}

// PR checks
Utils.isMainBranch(this) && KogitoJobTemplate.createPullRequestMultibranchPipelineJob(this, "${jenkins_path}/Jenkinsfile")

// Init branch
createSetupBranchJob()

// Nightly jobs
Closure setup4AMCronTriggerJobParamsGetter = { script ->
    def jobParams = JobParamsUtils.DEFAULT_PARAMS_GETTER(script)
    jobParams.triggers = [ cron: 'H 4 * * *' ]
    return jobParams
}
Closure nightlyJobParamsGetter = isMainStream() ? JobParamsUtils.DEFAULT_PARAMS_GETTER : setup4AMCronTriggerJobParamsGetter
KogitoJobUtils.createNightlyBuildChainBuildAndDeployJobForCurrentRepo(this, '', true)
setupSpecificBuildChainNightlyJob('native', nightlyJobParamsGetter)
setupNightlyQuarkusIntegrationJob('quarkus-main', nightlyJobParamsGetter)
setupNightlyQuarkusIntegrationJob('quarkus-branch', nightlyJobParamsGetter)
setupNightlyQuarkusIntegrationJob('quarkus-lts', nightlyJobParamsGetter)
setupNightlyQuarkusIntegrationJob('native-lts', nightlyJobParamsGetter)

// Release jobs
setupReleaseDeployJob()
setupReleasePromoteJob()
setupPostReleaseJob()

if (isMainStream()) {
    KogitoJobUtils.createQuarkusUpdateToolsJob(this, 'kogito-examples', [
       properties: [ 'quarkus-plugin.version', 'quarkus.platform.version' ],
    ])

    // Quarkus 3
    if (EnvUtils.isEnvironmentEnabled(this, 'quarkus-3')) {
        // Deactivated due to ghprb not available on Apache Jenkins
        // TODO create PR job with branch source plugin
        // setupPrQuarkus3RewriteJob()
        setupStandaloneQuarkus3RewriteJob()
    }
}

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupNightlyQuarkusIntegrationJob(String envName, Closure defaultJobParamsGetter = JobParamsUtils.DEFAULT_PARAMS_GETTER) {
    KogitoJobUtils.createNightlyBuildChainIntegrationJob(this, envName, Utils.getRepoName(this), true, defaultJobParamsGetter)
}

void setupSpecificBuildChainNightlyJob(String envName, Closure defaultJobParamsGetter = JobParamsUtils.DEFAULT_PARAMS_GETTER) {
    KogitoJobUtils.createNightlyBuildChainBuildAndTestJobForCurrentRepo(this, envName, true, defaultJobParamsGetter)
}

void createSetupBranchJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-examples', JobType.SETUP_BRANCH, "${jenkins_path}/Jenkinsfile.setup-branch", 'Kogito Examples Init branch')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
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

void setupReleaseDeployJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-examples-deploy', JobType.RELEASE, "${jenkins_path}/Jenkinsfile.deploy", 'Kogito Examples Deploy')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        PROPERTIES_FILE_NAME: 'deployment.properties',

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GITHUB_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_UPLOAD_REPOSITORY_URL}",
        MAVEN_REPO_CREDS_ID: "${MAVEN_ARTIFACTS_UPLOAD_REPOSITORY_CREDS_ID}",

        NEXUS_RELEASE_URL: "${MAVEN_NEXUS_RELEASE_URL}",
        NEXUS_RELEASE_REPOSITORY_ID: "${MAVEN_NEXUS_RELEASE_REPOSITORY}",
        NEXUS_STAGING_PROFILE_ID: "${MAVEN_NEXUS_STAGING_PROFILE_ID}",
        NEXUS_BUILD_PROMOTION_PROFILE_ID: "${MAVEN_NEXUS_BUILD_PROMOTION_PROFILE_ID}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            booleanParam('SKIP_TESTS', false, 'Skip tests')

            booleanParam('CREATE_PR', false, 'Should we create a PR with the changes ?')

            stringParam('PROJECT_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('DROOLS_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('KOGITO_PR_BRANCH', '', 'PR branch name')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}

void setupReleasePromoteJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-examples-promote', JobType.RELEASE, "${jenkins_path}/Jenkinsfile.promote", 'Kogito Examples Promote')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        PROPERTIES_FILE_NAME: 'deployment.properties',

        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",

        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        GITHUB_TOKEN_CREDS_ID: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}",

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
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
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

void setupPrQuarkus3RewriteJob() {
    def jobParams = JobParamsUtils.getBasicJobParamsWithEnv(this, 'kogito-examples.rewrite', JobType.PULL_REQUEST, 'quarkus-3', "${jenkins_path}/Jenkinsfile.quarkus-3.rewrite.pr", 'Kogito Examples Quarkus 3 rewrite patch regeneration')
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.jenkinsfile = "${jenkins_path}/Jenkinsfile.quarkus-3.rewrite.pr"
    jobParams.pr.putAll([
        run_only_for_branches: [ "${GIT_BRANCH}" ],
        disable_status_message_error: true,
        disable_status_message_failure: true,
        trigger_phrase: '.*[j|J]enkins,?.*(rewrite|write) [Q|q]uarkus-3.*',
        trigger_phrase_only: true,
        commitContext: 'Quarkus 3 rewrite',
    ])
    jobParams.env.putAll([
        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
    ])
    KogitoJobTemplate.createPRJob(this, jobParams)
}

void setupStandaloneQuarkus3RewriteJob() {
    def jobParams = JobParamsUtils.getBasicJobParams(this, 'kogito-examples.quarkus-3.rewrite', JobType.TOOLS, "${jenkins_path}/Jenkinsfile.quarkus-3.rewrite.standalone", 'Kogito Examples Quarkus 3 rewrite patch regeneration')
    jobParams.env.putAll(EnvUtils.getEnvironmentEnvVars(this, 'quarkus-3'))
    JobParamsUtils.setupJobParamsAgentDockerBuilderImageConfiguration(this, jobParams)
    jobParams.env.putAll([
        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",
        BASE_BRANCH: Utils.getGitBranch(this),
        BASE_AUTHOR: Utils.getGitAuthor(this),
        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            booleanParam('IS_PR_SOURCE_BRANCH', false, 'Set to true if you are launching the job for a PR source branch')
            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}
