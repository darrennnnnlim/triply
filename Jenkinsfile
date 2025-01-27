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

        stage('SonarQube Analysis') {
            steps {
                sh 'echo "Running SonarQube..."'
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
                    sh '''
                        docker build -t triply-frontend:${BUILD_TAG} -f Dockerfile .
                        docker tag triply-frontend:${BUILD_TAG} triply-frontend:latest
                    '''
                }
            }
        }

        // TODO
        // docker build -t triply-backend:${BUILD_NUMBER} .
        stage('Build Backend Image') {
            steps {
                dir(env.BACKEND_DIR) {
                    sh '''
                        docker build -t triply-backend:${BUILD_TAG} .
                        docker tag triply-backend:${BUILD_TAG} triply-backend:latest
                    '''
                }
            }
        }
        
        stage('Deploy Docker Stack') {
            steps {
                sh 'docker stack deploy -c docker-compose.yml triply --with-registry-auth'
            }
        }

        stage('Update Docker Swarm') {
            steps {
                sh '''
                    docker service update --force triply_frontend
                    docker service update --force triply_backend
                '''
            }
        }

        // TODO
        // Might change this to cron job
        stage('Docker Image Cleanup') {
            steps {
                sh '''
                    docker image prune -af --filter "label=build-number!=${BUILD_TAG}" || true
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
