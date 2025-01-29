pipeline {
    agent any
    tools {
        maven 'Maven_3.9.9'
        nodejs 'NodeJS_22.13.0'
    }

    // Might shift this to Jenkins environment placeholder
    environment {
        FRONTEND_DIR = 'triply-app'
        BACKEND_DIR = 'triply-api'
        BUILD_TAG = "${env.BUILD_NUMBER}"
        DOCKER_REPO_FRONTEND = "darrennnnlim/triply-frontend"
        DOCKER_REPO_BACKEND = "darrennnnlim/triply-backend"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'git@github.com:darrennnnnlim/triply.git', credentialsId: env.GIT_CREDENTIAL_ID
            }
        }

        stage('Build & Test Frontend') {
            steps {
                sh 'echo "Running Frontend Build & Test..."'
                dir(env.FRONTEND_DIR) {
                    sh '''
                        npm install
                        npm run build
                    '''
                }
            }
        }

        stage('Build & Test Backend') {
            steps {
                sh 'echo "Running Frontend Build & Test..."'
                dir(env.BACKEND_DIR) {
                    sh 'mvn clean install'
                }
            }
        }

        stage('SonarCloud analysis') {
            steps {
                dir(env.BACKEND_DIR) {
                    withSonarQubeEnv(credentialsId: env.SONARCLOUD_CREDENTIAL_ID, installationName: 'SonarCloud') {
                        sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar'
                    }
                }
            }
        }

        stage('Snyk Security Scan') {
            steps {
                sh 'echo "Running Snyk Security Scan..."'
            }
        }

        // TODO
        // docker build -t triply-frontend:${BUILD_NUMBER} .
        stage('Build Frontend Image') {
            steps {
                dir(env.FRONTEND_DIR) {
                    sh 'docker build -t ${DOCKER_REPO_FRONTEND}:latest .'
                }
            }
        }

        // TODO
        // docker build -t triply-backend:${BUILD_NUMBER} .
        stage('Build Backend Image') {
            steps {
                dir(env.BACKEND_DIR) {
                    sh 'docker build -t ${DOCKER_REPO_BACKEND}:latest .'
                }
            }
        }

        stage('Login to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIAL_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASSWORD')]) {
                    sh '''
                        echo $DOCKER_PASSWORD | docker login -u $DOCKER_USER --password-stdin
                    '''
                }
            }
        }

        stage('Push Docker Images to Docker Hub') {
            steps {
                dir(env.BACKEND_DIR) {
                    sh '''
                        docker push ${DOCKER_REPO_FRONTEND}:latest
                        docker push ${DOCKER_REPO_BACKEND}:latest
                    '''
                }
            }
        }
        
        stage('Deploy Docker Stack') {
            steps {
                sh 'docker stack deploy -c docker-compose.yml triply --with-registry-auth'
            }
        }

        // TODO
        // Might shift this to post-success
        stage('Docker Container Cleanup') {
            steps {
                sh 'docker container prune -f'
            }
        }

        // TODO
        // Might change this to cron job
        stage('Docker Image Cleanup') {
            steps {
                sh '''
                    docker images --format "{{.Repository}}:{{.Tag}} {{.ID}}" | while read repo_tag id; do
                        if [[ "$repo_tag" == triply-backend* || "$repo_tag" == triply-frontend* ]]; then
                            build_number=$(docker inspect "$id" | grep -i '"build-number":' | awk -F '"' '{print $4}')
                            if [ "$build_number" != "${BUILD_TAG}" ]; then
                                echo "Removing image: $id from $repo_tag with build-number $build_number"
                                docker rmi -f "$id"
                            fi
                        fi
                    done
                '''
            }
        }
    }

    post {
        always {
            echo "Pipeline finished!"
        }
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}
