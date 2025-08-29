pipeline {
    agent any
    parameters {
        string(name: 'TAG', defaultValue: 'latest', description: 'Docker image tag')
        string(name: 'BRANCH', defaultValue: 'mysql-for-products', description: 'Git branch to checkout')
    }
    environment {
        TAG = "${params.TAG}"
        BRANCH = "${params.BRANCH}"
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        VM_SSH_KEY = credentials('production-vm-ssh-key')
        VM_HOST = "${env.VM_HOST ?: '192.168.56.50'}"
        VM_USER = "${env.VM_USER ?: 'deploy'}"
        VM_PORT = "${env.VM_PORT ?: '22'}"
    }
    stages {
        stage('Set Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${params.BRANCH}-${params.TAG}"
                }
            }
        }
    
        stage('Check Environment') {
            steps {
                sh '''
                    echo "Branch: $BRANCH"
                    echo "Docker Hub User (will be used as DOCKER_USER): $DOCKERHUB_CREDENTIALS_USR"
                    echo "TAG: $TAG"
                    echo "VM Host: $VM_HOST"
                    echo "VM User: $VM_USER"
                    echo "VM Port: $VM_PORT"
                    env | grep DOCKER || echo "No DOCKER vars"
                '''
            }
        }
        stage('Build') {
            steps {
                sh '''#!/bin/bash
                    sed -i "s/DOCKER_USER/$DOCKERHUB_CREDENTIALS_USR/g" docker-compose.yml
                    sed -i "s/TAG/$TAG/g" docker-compose.yml
                    cat docker-compose.yml
                '''
                sh 'docker compose build'
            }
        }
        stage('Push to Registry') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "Logging in as: $DOCKER_USER"
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        
                        echo "Pushing images to Docker Hub..."
                        
                        # Push each service individually for better error handling
                        for service in ad-service cart-service product-catalog-service recommendation-service shipping-service currency-service payment-service email-service checkout-service frontend; do
                            echo "Pushing $DOCKER_USER/${service}:$TAG"
                            docker push "$DOCKER_USER/${service}:$TAG" || echo "Failed to push $service, continuing..."
                        done
                        
                        echo "Logout from Docker Hub"
                        docker logout
                    '''
                }
            }
        }
        // Deploy to VirtualBox VM via SSH
        stage('Deploy to VM') {
            steps {
                sh '''
                    # Setup SSH key
                    mkdir -p ~/.ssh
                    cp "$VM_SSH_KEY" ~/.ssh/vm_key
                    chmod 600 ~/.ssh/vm_key
                    
                    # Clean and create deploy directory on VM
                    ssh -i ~/.ssh/vm_key -o StrictHostKeyChecking=no -p $VM_PORT $VM_USER@$VM_HOST "rm -rf ~/deploy && mkdir -p ~/deploy"
                    
                    # Copy docker-compose.yml and mysql-init to VM
                    scp -i ~/.ssh/vm_key -o StrictHostKeyChecking=no -P $VM_PORT docker-compose.yml $VM_USER@$VM_HOST:~/deploy/docker-compose.yml
                    scp -i ~/.ssh/vm_key -o StrictHostKeyChecking=no -P $VM_PORT -r mysql-init $VM_USER@$VM_HOST:~/deploy/
                    
                    # Deploy on VM
                    ssh -i ~/.ssh/vm_key -o StrictHostKeyChecking=no -p $VM_PORT $VM_USER@$VM_HOST "
                        cd ~/deploy && 
                        echo 'Stopping existing containers...' &&
                        docker compose down --remove-orphans || true &&
                        echo 'Pulling latest images...' &&
                        docker compose pull &&
                        echo 'Starting containers...' &&
                        docker compose up -d &&
                        echo 'Deployment completed successfully!'
                    "
                    
                    # Clean up SSH key
                    rm -f ~/.ssh/vm_key
                '''
            }
        }
    }
}
