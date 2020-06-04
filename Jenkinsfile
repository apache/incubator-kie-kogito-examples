@Library('jenkins-pipeline-shared-libraries')_

pipeline {
    agent {
        label 'kie-rhel7 && kie-mem16g'
    }
    tools {
        maven 'kie-maven-3.6.2'
        jdk 'kie-jdk11'
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')
        timeout(time: 120, unit: 'MINUTES')
    }
    environment {
        MAVEN_OPTS = '-Xms1024m -Xmx4g'
    }
    stages {
        stage('Build kogito-runtimes') {
            steps {
                dir("kogito-runtimes") {
                    script {
                        githubscm.checkoutIfExists('kogito-runtimes', "$CHANGE_AUTHOR", "$CHANGE_BRANCH", 'kiegroup', "$CHANGE_TARGET", true)
                        maven.runMavenWithSubmarineSettings('clean install', true)
                    }
                }
            }
        }
        stage('Build kogito-apps') {
            steps {
                dir("kogito-apps") {
                    script {
                        githubscm.checkoutIfExists('kogito-apps', "$CHANGE_AUTHOR", "$CHANGE_BRANCH", 'kiegroup', "$CHANGE_TARGET", true)
                        maven.runMavenWithSubmarineSettings('clean install', true)
                    }
                }
            }
        }
        stage('Build kogito-examples') {
            steps {
                dir("kogito-examples") {
                    script {
                        githubscm.checkoutIfExists('kogito-runtimes', "$CHANGE_AUTHOR", "$CHANGE_BRANCH", 'kiegroup', "$CHANGE_TARGET", true)
                        maven.runMavenWithSubmarineSettings('clean install', false)
                    }
                }
                // Use a separate dir for persistence to not overwrite the test results
                dir("kogito-examples-persistence") {
                    script {
                        githubscm.checkoutIfExists('kogito-examples', "$CHANGE_AUTHOR", "$CHANGE_BRANCH", 'kiegroup', "$CHANGE_TARGET", true)
                        // Don't run with tests so far, see: https://github.com/quarkusio/quarkus/issues/6885
                        maven.runMavenWithSubmarineSettings('clean install -Ppersistence', true)
                    }
                }
            }
        }
    }
    post {
        unstable {
            script {
                mailer.sendEmailFailure()
            }
        }
        failure {
            script {
                mailer.sendEmailFailure()
            }
        }
        always {
            junit '**/target/surefire-reports/**/*.xml'
            cleanWs()
        }
    }
}
