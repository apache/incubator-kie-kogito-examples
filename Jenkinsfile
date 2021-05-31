@Library('jenkins-pipeline-shared-libraries')_

import org.kie.jenkins.MavenCommand

changeAuthor = env.ghprbPullAuthorLogin ?: CHANGE_AUTHOR
changeBranch = env.ghprbSourceBranch ?: CHANGE_BRANCH
changeTarget = env.ghprbTargetBranch ?: CHANGE_TARGET

quarkusRepo = 'quarkus'
kogitoRuntimesRepo = 'kogito-runtimes'
optaplannerRepo = 'optaplanner'
kogitoExamplesRepo = 'kogito-examples'

pipeline {
    agent {
        label 'kie-rhel7 && kie-mem16g'
    }
    tools {
        maven 'kie-maven-3.6.2'
        jdk 'kie-jdk11'
    }
    options {
        timestamps()
        timeout(time: getTimeoutValue(), unit: 'MINUTES')
    }
    environment {
        MAVEN_OPTS = '-Xms1024m -Xmx4g'
    }
    stages {
        stage('Initialize') {
            steps {
                checkoutKogitoRepo(kogitoRuntimesRepo)
                checkoutOptaplannerRepo()
                checkoutKogitoRepo(kogitoExamplesRepo)
            }
        }
        stage('Build quarkus') {
            when {
                expression { return getQuarkusBranch() }
            }
            steps {
                script {
                    checkoutQuarkusRepo()
                    runQuickBuild(quarkusRepo)
                }
            }
        }
        stage('Build Runtimes') {
            steps {
                script {
                    runQuickBuild(kogitoRuntimesRepo)
                }
            }
        }
        stage('Build Optaplanner') {
            steps {
                script {
                    runQuickBuild(optaplannerRepo)
                }
            }
        }
        stage('Examples Build&Test') {
            steps {
                script {
                    runUnitTests(kogitoExamplesRepo)
                }
            }
        }
        stage('Examples Integration Tests') {
            steps {
                script {
                    runIntegrationTests(kogitoExamplesRepo)
                }
            }
        }
        stage('Examples Integration Tests with persistence') {
            steps {
                script {
                    runIntegrationTests(kogitoExamplesRepo, ['persistence'])
                }
            }
        }
        stage('Examples Integration Tests with events') {
            steps {
                script {
                    runIntegrationTests(kogitoExamplesRepo, ['events'])
                }
            }
        }
    }
    post {
        failure {
            script {
                mailer.sendEmail_failedPR()
            }
        }
        unstable {
            script {
                mailer.sendEmail_unstablePR()
            }
        }
        fixed {
            script {
                mailer.sendEmail_fixedPR()
            }
        }
        cleanup {
            script {
                util.cleanNode('docker')
            }
        }
    }
}

void checkoutKogitoRepo(String repo, String dirName=repo) {
    dir(dirName) {
        githubscm.checkoutIfExists(repo, changeAuthor, changeBranch, 'kiegroup', getKogitoTargetBranch(), true)
    }
}

void checkoutOptaplannerRepo() {
    dir('optaplanner') {
        githubscm.checkoutIfExists('optaplanner', changeAuthor, changeBranch, 'kiegroup', getOptaplannerTargetBranch(), true)
    }
}

void checkoutQuarkusRepo() {
    dir('quarkus') {
        checkout(githubscm.resolveRepository('quarkus', 'quarkusio', getQuarkusBranch(), false))
    }
}

String getKogitoTargetBranch() {
    return getTargetBranch(isUpstreamOptaplannerProject() ? -7 : 0)
}

String getOptaplannerTargetBranch() {
    return getTargetBranch(isUpstreamKogitoProject() ? 7 : 0)
}

String getTargetBranch(Integer addToMajor) {
    String targetBranch = changeTarget
    String [] versionSplit = targetBranch.split("\\.")
    if (versionSplit.length == 3
        && versionSplit[0].isNumber()
        && versionSplit[1].isNumber()
        && versionSplit[2] == 'x') {
        targetBranch = "${Integer.parseInt(versionSplit[0]) + addToMajor}.${versionSplit[1]}.x"
    } else {
        echo "Cannot parse changeTarget as release branch so going further with current value: ${changeTarget}"
        }
    return targetBranch
}

MavenCommand getMavenCommand(String directory, boolean addQuarkusVersion=true, boolean canNative = false) {
    mvnCmd = new MavenCommand(this, ['-fae'])
                .withSettingsXmlId('kogito_release_settings')
                .withProperty('java.net.preferIPv4Stack', true)
                .inDirectory(directory)
    if (addQuarkusVersion && getQuarkusBranch()) {
        mvnCmd.withProperty('version.io.quarkus', '999-SNAPSHOT')
    }
    if (canNative && isNative()) {
        mvnCmd.withProfiles(['native'])
            .withProperty('quarkus.native.container-build', true)
            .withProperty('quarkus.native.container-runtime', 'docker')
            .withProperty('quarkus.profile', 'native') // Added due to https://github.com/quarkusio/quarkus/issues/13341
    }
    return mvnCmd
}

void saveReports() {
    junit(testResults: '**/target/surefire-reports/**/*.xml', allowEmptyResults: true)
    junit(testResults: '**/target/failsafe-reports/**/*.xml', allowEmptyResults: true)
}

void cleanContainers() {
    cloud.cleanContainersAndImages('docker')
}

String getQuarkusBranch() {
    return env['QUARKUS_BRANCH']
}

boolean isNative() {
    return env['NATIVE'] && env['NATIVE'].toBoolean()
}

boolean isDownstreamJob() {
    return env['DOWNSTREAM_BUILD'] && env['DOWNSTREAM_BUILD'].toBoolean()
}

String getUpstreamTriggerProject() {
    return env['UPSTREAM_TRIGGER_PROJECT']
}

boolean isNormalPRCheck() {
    return !(isDownstreamJob() || getQuarkusBranch() || isNative())
}

boolean isUpstreamKogitoProject() {
    return getUpstreamTriggerProject() && getUpstreamTriggerProject().startsWith('kogito')
}

boolean isUpstreamOptaplannerProject() {
    return getUpstreamTriggerProject() && getUpstreamTriggerProject().startsWith('opta')
}

Integer getTimeoutValue() {
    return isNative() ? 600 : 240
}

void runQuickBuild(String project) {
    getMavenCommand(project, false, false)
            .withProperty('quickly')
            .run('clean install')
}

void runUnitTests(String project) {
    def mvnCmd = getMavenCommand(project)
    if (project == 'optaplanner') {
        mvnCmd.withProperty('enforcer.skip')
            .withProperty('formatter.skip')
            .withProperty('impsort.skip')
            .withProperty('revapi.skip')
    } else {
        mvnCmd.withProperty('quickTests')
    }

    runMavenTests(mvnCmd, 'clean install')
}

void runIntegrationTests(String project, List profiles=[]) {
    String profileSuffix = profiles ? "-${profiles.join('-')}" : ''
    String itFolder = "${project}-it${profileSuffix}"
    sh "cp -r ${project} ${itFolder}"

    runMavenTests(getMavenCommand(itFolder).withProfiles(profiles), 'verify')
}

void runMavenTests(MavenCommand mvnCmd, String mvnRunCmd) {
    try {
        mvnCmd.run(mvnRunCmd)
    } catch (err) {
        throw err
    } finally {
        saveReports()
        cleanContainers()
    }
}
