pipeline {
    agent any 
    stages {
        stage('Test docker') {
            steps {
                sh 'docker --version'
                sh 'docker ps'
            }
        }
        stage('Build frontend') {
            steps {
                sh 'docker build -t frontend-test ./src/frontend''
            }
        }
    }
}