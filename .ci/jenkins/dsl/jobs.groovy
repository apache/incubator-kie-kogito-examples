import org.kie.jenkins.jobdsl.templates.KogitoJobTemplate
import org.kie.jenkins.jobdsl.FolderUtils
import org.kie.jenkins.jobdsl.KogitoJobType
import org.kie.jenkins.jobdsl.KogitoJobUtils
import org.kie.jenkins.jobdsl.Utils

JENKINSFILE_PATH = '.ci/jenkins'

def getDefaultJobParams() {
    return KogitoJobTemplate.getDefaultJobParams(this, 'kogito-examples')
}

def getJobParams(String jobName, String jobFolder, String jenkinsfileName, String jobDescription = '') {
    def jobParams = getDefaultJobParams()
    jobParams.job.name = jobName
    jobParams.job.folder = jobFolder
    jobParams.jenkinsfile = jenkinsfileName
    if (jobDescription) {
        jobParams.job.description = jobDescription
    }
    return jobParams
}

Map getMultijobPRConfig() {
    return [
        parallel: true,
        buildchain: true,
        jobs : [
            [
                id: 'kogito-examples',
                primary: true,
                env : [
                    // Sonarcloud analysis is disabled for examples
                    DISABLE_SONARCLOUD: true,
                ]
            ]
        ]
    ]
}

if (Utils.isMainBranch(this)) {
    // For BDD runtimes PR job
    setupDeployJob(FolderUtils.getPullRequestRuntimesBDDFolder(this), KogitoJobType.PR)
}

// PR checks
setupMultijobPrDefaultChecks()
setupMultijobPrNativeChecks()
setupMultijobPrLTSChecks()

// Nightly jobs
setupNativeJob()
setupDeployJob(FolderUtils.getNightlyFolder(this), KogitoJobType.NIGHTLY)
setupPromoteJob(FolderUtils.getNightlyFolder(this), KogitoJobType.NIGHTLY)

// No release directly on main branch
if (!Utils.isMainBranch(this)) {
    setupDeployJob(FolderUtils.getReleaseFolder(this), KogitoJobType.RELEASE)
    setupPromoteJob(FolderUtils.getReleaseFolder(this), KogitoJobType.RELEASE)
}

if (Utils.isLTSBranch(this)) {
    setupNativeLTSJob()
}

KogitoJobUtils.createQuarkusUpdateToolsJob(this, 'kogito-examples', 'Kogito Examples', [
  properties: [ 'quarkus-plugin.version', 'quarkus.platform.version' ],
])

/////////////////////////////////////////////////////////////////
// Methods
/////////////////////////////////////////////////////////////////

void setupMultijobPrDefaultChecks() {
    KogitoJobTemplate.createMultijobPRJobs(this, getMultijobPRConfig()) { return getDefaultJobParams() }
}

void setupMultijobPrNativeChecks() {
    KogitoJobTemplate.createMultijobNativePRJobs(this, getMultijobPRConfig()) { return getDefaultJobParams() }
}

void setupMultijobPrLTSChecks() {
    KogitoJobTemplate.createMultijobLTSPRJobs(this, getMultijobPRConfig()) { return getDefaultJobParams() }
}

void setupNativeJob() {
    def jobParams = getJobParams('kogito-examples-native', FolderUtils.getNightlyFolder(this), "${JENKINSFILE_PATH}/Jenkinsfile.native", 'Kogito Examples Native Testing')
    jobParams.triggers = [ cron : 'H 6 * * *' ]
    KogitoJobTemplate.createPipelineJob(this, jobParams).with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
        }
        environmentVariables {
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
            env('NOTIFICATION_JOB_NAME', 'Native check')
        }
    }
}

void setupNativeLTSJob() {
    def jobParams = getJobParams('kogito-examples-native-lts', FolderUtils.getNightlyFolder(this), "${JENKINSFILE_PATH}/Jenkinsfile.native", 'Kogito Examples Native LTS Testing')
    jobParams.triggers = [ cron : 'H 8 * * *' ]
    KogitoJobTemplate.createPipelineJob(this, jobParams).with {
        parameters {
            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')

            stringParam('NATIVE_BUILDER_IMAGE', Utils.getLTSNativeBuilderImage(this), 'Which native builder image to use ?')
        }
        environmentVariables {
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
            env('NOTIFICATION_JOB_NAME', 'Native LTS check')
        }
    }
}

/*
* Setup deploy job
* when using `isForPr` property, then git branch/author information are parameters instead of env
* as we want in that case more dynamic git retrieval
* also we set a specific repository for the pr checks
*/
void setupDeployJob(String jobFolder, KogitoJobType jobType) {
    def jobParams = getJobParams('kogito-examples-deploy', jobFolder, "${JENKINSFILE_PATH}/Jenkinsfile.deploy", 'Kogito Examples Deploy')
    if (jobType == KogitoJobType.PR) {
        jobParams.git.branch = '${BUILD_BRANCH_NAME}'
        jobParams.git.author = '${GIT_AUTHOR}'
        jobParams.git.project_url = Utils.createProjectUrl("${GIT_AUTHOR_NAME}", jobParams.git.repository)
    }
    KogitoJobTemplate.createPipelineJob(this, jobParams).with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')
            if (jobType == KogitoJobType.PR) {
                // author can be changed as param only for PR behavior, due to source branch/target, else it is considered as an env
                stringParam('GIT_AUTHOR', "${GIT_AUTHOR_NAME}", 'Set the Git author to checkout')
            }

            booleanParam('SKIP_TESTS', false, 'Skip tests')

            booleanParam('CREATE_PR', false, 'Should we create a PR with the changes ?')
            booleanParam('UPDATE_NIGHTLY_BRANCH', false, 'Set to true if at the end of the run, the nightly branch should be updated. This CANNOT be used with `CREATE_PR` parameter also enabled (this latter one has priority). It is also disabled for release job.')

            stringParam('PROJECT_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('OPTAPLANNER_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }

        environmentVariables {
            env('REPO_NAME', 'kogito-examples')
            env('PROPERTIES_FILE_NAME', 'deployment.properties')

            env('RELEASE', jobType == KogitoJobType.RELEASE)
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
            env('MAVEN_SETTINGS_CONFIG_FILE_ID', "${MAVEN_SETTINGS_FILE_ID}")

            if (jobType == KogitoJobType.PR) {
                env('MAVEN_DEPENDENCIES_REPOSITORY', "${MAVEN_PR_CHECKS_REPOSITORY_URL}")
                env('MAVEN_DEPLOY_REPOSITORY', "${MAVEN_PR_CHECKS_REPOSITORY_URL}")
                env('MAVEN_REPO_CREDS_ID', "${MAVEN_PR_CHECKS_REPOSITORY_CREDS_ID}")
            } else {
                env('GIT_AUTHOR', "${GIT_AUTHOR_NAME}")

                env('AUTHOR_CREDS_ID', "${GIT_AUTHOR_CREDENTIALS_ID}")
                env('GITHUB_TOKEN_CREDS_ID', "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}")
                env('GIT_AUTHOR_BOT', "${GIT_BOT_AUTHOR_NAME}")
                env('BOT_CREDENTIALS_ID', "${GIT_BOT_AUTHOR_CREDENTIALS_ID}")

                env('MAVEN_DEPENDENCIES_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
                env('MAVEN_DEPLOY_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
                if (jobType == KogitoJobType.RELEASE) {
                    env('NEXUS_RELEASE_URL', "${MAVEN_NEXUS_RELEASE_URL}")
                    env('NEXUS_RELEASE_REPOSITORY_ID', "${MAVEN_NEXUS_RELEASE_REPOSITORY}")
                    env('NEXUS_STAGING_PROFILE_ID', "${MAVEN_NEXUS_STAGING_PROFILE_ID}")
                    env('NEXUS_BUILD_PROMOTION_PROFILE_ID', "${MAVEN_NEXUS_BUILD_PROMOTION_PROFILE_ID}")
                }
            }
        }
    }
}

void setupPromoteJob(String jobFolder, KogitoJobType jobType) {
    KogitoJobTemplate.createPipelineJob(this, getJobParams('kogito-examples-promote', jobFolder, "${JENKINSFILE_PATH}/Jenkinsfile.promote", 'Kogito Examples Promote')).with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            // Deploy job url to retrieve deployment.properties
            stringParam('DEPLOY_BUILD_URL', '', 'URL to jenkins deploy build to retrieve the `deployment.properties` file. If base parameters are defined, they will override the `deployment.properties` information')

            // Release information which can override `deployment.properties`
            stringParam('PROJECT_VERSION', '', 'Override `deployment.properties`. Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('OPTAPLANNER_VERSION', '', 'Override `deployment.properties`. Optional if not RELEASE. If RELEASE, cannot be empty.')
            stringParam('GIT_TAG', '', 'Git tag to set, if different from PROJECT_VERSION')
            booleanParam('UPDATE_STABLE_BRANCH', false, 'Set to true if you want to update the `stable` branch to new created Git tag.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }

        environmentVariables {
            env('REPO_NAME', 'kogito-examples')
            env('PROPERTIES_FILE_NAME', 'deployment.properties')

            env('RELEASE', jobType == KogitoJobType.RELEASE)
            env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")

            env('GIT_AUTHOR', "${GIT_AUTHOR_NAME}")

            env('AUTHOR_CREDS_ID', "${GIT_AUTHOR_CREDENTIALS_ID}")
            env('GITHUB_TOKEN_CREDS_ID', "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}")
            env('GIT_AUTHOR_BOT', "${GIT_BOT_AUTHOR_NAME}")
            env('BOT_CREDENTIALS_ID', "${GIT_BOT_AUTHOR_CREDENTIALS_ID}")

            env('MAVEN_SETTINGS_CONFIG_FILE_ID', "${MAVEN_SETTINGS_FILE_ID}")
            env('MAVEN_DEPENDENCIES_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
            env('MAVEN_DEPLOY_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
        }
    }
}
