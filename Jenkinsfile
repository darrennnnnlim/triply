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
        DEPLOYMENT_DIR = 'triply-deployment'
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
                    sh 'docker build -t triply-frontend:latest .'
                }
            }
        }

        // TODO
        // docker build -t triply-backend:${BUILD_NUMBER} .
        stage('Build Backend Image') {
            steps {
                dir(env.BACKEND_DIR) {
                    sh 'docker build -t triply-backend:latest .'
                }
            }
        }
        
        stage('Deploy Stack') {
            steps {
                sh 'docker stack deploy -c docker-compose.yml triply'
            }
        }

        // TODO
        // Might change this to cron job
        stage('Docker Image Cleanup') {
            steps {
                sh 'docker image prune -f'
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
