pipeline {
    agent any

    stages {

        stage('PULL STAGE 2') {
            steps {
                git 'https://github.com/chetanraval07/BB2728-Final.git'
            }
        }

        stage('FRONTEND-DOCKER-BUILD') {
            steps {
                sh '''
                cd frontend
                docker build -t shivansh7310/easy-frontend:latest .
                '''
            }
        }

        stage('BACKEND-DOCKER-BUILD') {
            steps {
                sh '''
                cd backend
                docker build -t shivansh7310/easy-backend:latest .
                '''
            }
        }

        stage('DOCKER-PUSH') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh '''
                    echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                    docker push shivansh7310/easy-frontend:latest
                    docker push shivansh7310/easy-backend:latest

                    docker logout
                    '''
                }
            }
        }

        stage('DOCKER-CLEAN') {
            steps {
                sh '''
                docker rmi -f shivansh7310/easy-frontend:latest || true
                docker rmi -f shivansh7310/easy-backend:latest || true
                '''
            }
        }

        stage('DEPLOY') {
           steps {
               sh '''
        aws eks update-kubeconfig \
          --region eu-central-1 \
          --name my-cluster

        kubectl get nodes

        kubectl apply -f simple-deploy/ '''
           }
        }
    }
    
}
