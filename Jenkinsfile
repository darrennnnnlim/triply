pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'git@github.com:darrennnnnlim/triply.git', credentialsId: env.GIT_CREDENTIAL_ID
            }
        }

        stage('Build & Test backend') {
            steps {
                sh 'echo "Running Backend Build & Test..."'
            }
        }

        stage('Build & Test Frontend') {
            steps {
                sh 'echo "Running Frontend Build & Test..."'
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

        stage('Docker Build') {
            steps {
                sh 'echo "Running Docker Build..."'
            }
        }

        stage('Push to ECR') {
            steps {
                sh 'echo "Running Push to ECR..."'
            }
        }

        stage('Deploy to ECS Fargate') {
            steps {
                sh 'echo "Running Deploy to ECS Fargate..."'
            }
        }

    }

    post {
        always {
            echo "Pipeline finished!"
        }
    }
}
