#!groovy
import groovy.json.JsonOutput

def fileExists(filePath) {
    def fileCheck = sh(script: "test -d ${filePath}", returnStatus: true)
    return fileCheck == 0
}

pipeline {
    agent any
    parameters {
        gitParameter name: 'BRANCH', type: 'PT_BRANCH'
    }
    environment {
        SLACK_CHANNEL = '#배포방'
        BUILD_USER_ID = "${BUILD_USER_ID}"
        BUILD_USER = "${BUILD_USER}"
    }
    stages {
        stage('check build user') {
                steps {
                      wrap([$class: 'BuildUser']) {
                          script {
                              BUILD_USER_ID = "${BUILD_USER_ID}"
                              BUILD_USER = "${BUILD_USER}"
                          }
                      }
                    echo "Build User ID: ${BUILD_USER_ID}"
                    echo "Build User: ${BUILD_USER}"
                }
        }
        stage('checkout') {
                    steps {
                        checkout(
                                [
                                        $class                           : 'GitSCM',
                                        branches                         : [[name: '$BRANCH_NAME']],
                                        doGenerateSubmoduleConfigurations: false,
                                        extensions                       : [],
                                        submoduleCfg                     : [],
                                        userRemoteConfigs                : [[credentialsId : 'Admin', name:'origin',url: 'https://github.com/RadSparkle/best-pie_scraper.git']]
                                ]
                                )
                    }
        }
        stage('Start') {
                    steps {
                        slackSend (channel: SLACK_CHANNEL, color: '#FFFF00', message: "배포 시작: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}) $BUILD_USER")
                           }
        }
        stage('Build & Release') {
                    steps {
                        script {
                            try {
                                sh ("chmod 755 ./gradlew")
                                sh ("./gradlew clean bootJar")
                                env.jarfile = sh (script: 'basename build/libs/*.jar .jar', returnStdout: true ).trim()
                                echo "set File ${env.jarfile}.jar"
                                sh ("ls -la")
                                sh ("whoami")

                                def directoryPath = "/home/best_pie/api/scraper"
                                if(!fileExists(directoryPath)) {
                                     sh "sudo mkdir -p ${directoryPath}"
                                     echo "Directory created at ${directoryPath}"
                                }
                                sh "sudo chmod 777 ${directoryPath}"
                                sh "cp build/libs/*.jar ${directoryPath}/${env.jarfile}.jar"
                                echo "Restart best_pi API"
                                sh "sudo ${directoryPath}/bin/scraper.sh restart"
                            } catch (e) {
                                slackSend (channel: SLACK_CHANNEL, color: '#FF0000', message: "빌드 실패: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
                            }
                        }
                    }
        }
    }
    post {
        success {
            slackSend (channel: SLACK_CHANNEL, color: '#00FF00', message: "배포 성공: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}) $BUILD_USER" )
        }
        failure {
            slackSend (channel: SLACK_CHANNEL, color: '#FF0000', message: "배포 실패: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}) $BUILD_USER")
        }
    }
}