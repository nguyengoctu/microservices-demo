pipeline {
    agent any
    environment {
        TAG = "${params.TAG}"
    }

    stages {
        stage('Check Environment') {
            steps {
                sh '''
                    echo "DOCKER_USER from Jenkins: [$DOCKER_USER]"
                    env | grep DOCKER || echo "No DOCKER vars"
                    echo "TAG: $TAG"
                '''
            }
        }
        stage('Test connectivity') {
            steps {
                sh 'docker --version'
                sh 'docker ps'
            }
        }
        stage('Build') {
            steps {
                sh '''#!/bin/bash
                    sed -i "s/DOCKER_USER/$DOCKER_USER/g" docker-compose.yml
                    sed -i "s/TAG/$TAG/g" docker-compose.yml
                    cat docker-compose.yml
                '''
                sh 'docker compose build'
            }
        }
        stage('Push to Registry') {
            steps {
                sh '''
                    docker compose push
                '''
            }
        }
        // stage deploy another wsl via ssh, and copy the docker-compose.yml there, then pull images and run
        stage('Deploy to Server') {
            steps {
                sh '''
                    scp -o StrictHostKeyChecking=no -P 2223 docker-compose.yml $DEPLOY_USER@$DEPLOY_HOST:deploy/docker-compose.yml
                    ssh -o StrictHostKeyChecking=no -p 2223 $DEPLOY_USER@$DEPLOY_HOST "cd deploy && docker compose pull && docker compose up -d"
                '''
            }
        }
    }
}
