pipeline {
    agent any 
    stages {
        stage('Check Environment') {
            steps {
                sh '''
                    echo "DOCKER_USER from Jenkins: [$DOCKER_USER]"
                    env | grep DOCKER || echo "No DOCKER vars"
                '''
            }
        }
        stage('Test connectivity') {
            steps {
                sh 'docker --version'
                sh 'docker ps'
            }
        }
        stage('Build frontend') {
            steps {
                sh 'docker build -t ${DOCKER_USER}/frontend:${BUILD_NUMBER} ./src/frontend'
            }
        }
        stage('Push to Registry') {
            steps {
                sh '''
                    docker push $DOCKER_USER/frontend:${BUILD_NUMBER}
                    docker tag $DOCKER_USER/frontend:${BUILD_NUMBER} $DOCKER_USER/frontend:latest
                    docker push $DOCKER_USER/frontend:latest
                '''
            }
        }
    }
}