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
        timeout(time: env.TIMEOUT_VALUE, unit: 'MINUTES')
    }
    environment {
        MAVEN_OPTS = '-Xms1024m -Xmx4g'
    }
    stages {
        stage('Initialize') {
            steps {
                checkoutRepo(kogitoRuntimesRepo)
                checkoutRepo(optaplannerRepo)
                checkoutRepo(kogitoExamplesRepo)
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
                    runIntegrationTests(kogitoExamplesRepo, ['persistence'])
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

void checkoutRepo(String repo) {
    checkoutRepo(repo, changeAuthor, changeBranch, repo == optaplannerRepo ? getOptaplannerReleaseBranch(changeTarget) : changeTarget)
}

String getOptaplannerReleaseBranch(String branch) {
    String checkedBranch = branch
    String [] versionSplit = checkedBranch.split("\\.")
    if (versionSplit.length == 3
        && versionSplit[0].isNumber()
        && versionSplit[1].isNumber()
       && versionSplit[2] == 'x') {
        checkedBranch = "${Integer.parseInt(versionSplit[0]) + 7}.${versionSplit[1]}.x"
    } else {
        echo "Cannot parse branch as release branch so going further with current value: ${checkedBranch}"
       }
    return checkedBranch
}

void checkoutRepo(String repo, String author, String branch, String targetBranch = '') {
    dir(repo) {
        if (targetBranch) {
            githubscm.checkoutIfExists(repo, author, branch, 'kiegroup', targetBranch, true)
        } else {
            checkout(githubscm.resolveRepository(repo, author, branch, false))
        }
    }
}

void checkoutQuarkusRepo() {
    checkoutRepo(quarkusRepo, 'quarkusio', getQuarkusBranch())
}

MavenCommand getMavenCommand(String directory, boolean addQuarkusVersion = true, boolean canNative = true) {
    def mvnCmd = new MavenCommand(this, ['-fae'])
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
    junit '**/target/surefire-reports/**/*.xml, **/target/failsafe-reports/**/*.xml'
}

void cleanContainers() {
    cloud.cleanContainersAndImages('docker')
}

boolean isNative() {
    return env['NATIVE'] && env['NATIVE'].toBoolean()
}

String getQuarkusBranch() {
    return env['QUARKUS_BRANCH']
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
