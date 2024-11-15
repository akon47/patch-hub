pipeline {
    agent any

    tools {
        jdk("JAVA 17")
    }

    environment {
        dockerImage = ''
        APP_NAME = 'patch-hub'
        IMAGE_NAME = 'akon47/patch-hub'
        IMAGE_TAG = "${env.APP_VERSION}.${env.BUILD_NUMBER}"

        ACTIVE_PROFILE = 'prod'
        SPRING_PROD_PROPERTIES_PATH = "src/main/resources/application-${ACTIVE_PROFILE}.yml"

        GITHUB_CREDENTIALS_ID = 'git-hub'
        DOCKER_CREDENTIALS_ID = 'docker-hub'
    }

    stages {
        stage('Clone') {
            steps {
                echo 'Clonning Repository'
                git url: 'git@github.com:akon47/patch-hub.git', branch: 'master', credentialsId: GITHUB_CREDENTIALS_ID
            }
            post {
                success {
                    echo 'Successfully Cloned Repository'
                }
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }

        stage('Prepare') {
            steps {
                echo "Pre-Processing for ${ACTIVE_PROFILE} profile"
                script {

                }
            }
            post {
                success {
                    echo 'Successfully Pre-Processing'
                }
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }

        stage('Bulid Gradle') {
            steps {
                echo 'Bulid Gradle'
                dir('.') {
                    sh 'chmod +x gradlew'
                    sh "SPRING_PROFILES_ACTIVE=${ACTIVE_PROFILE} ./gradlew clean build -x test --info"
                }
            }
            post {
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }

        stage('Bulid Docker') {
            steps {
                echo 'Bulid Docker'
                script {
                    dockerImage = docker.build("${IMAGE_NAME}")
                }
            }
            post {
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }

        stage('Push Docker') {
            steps {
                echo 'Push Docker'
                script {
                    docker.withRegistry('', DOCKER_CREDENTIALS_ID) {
                        dockerImage.push("${IMAGE_TAG}")
                        dockerImage.push("latest")
                    }
                }
            }
            post {
                success {
                    sh 'docker rmi $(docker images -q -f dangling=true) || true'
                }
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }

        stage('Docker Run') {
            steps {
                echo 'Pull Docker Image & Docker Image Run'
                sshagent(credentials: ['ssh']) {
                    sh "ssh -o StrictHostKeyChecking=no kimhwan@10.10.10.145 'docker pull ${IMAGE_NAME}'"
                    sh "ssh -o StrictHostKeyChecking=no kimhwan@10.10.10.145 'docker ps -aq --filter name=${APP_NAME} | grep -q . && docker rm -f \$(docker ps -aq --filter name=${APP_NAME}) || true'"
                    sh "ssh -o StrictHostKeyChecking=no kimhwan@10.10.10.145 'docker run -d --restart always --name ${APP_NAME} -v /root/patch-hub/files:/var/files -v /root/patch-hub/config:/config -v /etc/localtime:/etc/localtime:ro -v /usr/share/zoneinfo/Asia/Seoul:/etc/timezone:ro --net=host ${IMAGE_NAME}'"
                    sh "ssh -o StrictHostKeyChecking=no kimhwan@10.10.10.145 'docker images -qf dangling=true | xargs -I{} docker rmi {} || true'"
                    sh "ssh -o StrictHostKeyChecking=no kimhwan@10.10.10.145 'docker rmi ${IMAGE_NAME}:${IMAGE_TAG} || true'"
                }
            }
            post {
                failure {
                    error 'This pipeline stops here...'
                }
            }
        }
    }
}
