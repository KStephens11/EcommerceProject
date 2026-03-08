pipeline {

    agent any

    tools {
        jdk 'Java25'
        maven 'maven'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                sh 'java -version'
                sh 'javac -version'
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                      mvn sonar:sonar \
                        -Dsonar.projectKey=EcommeerceProject
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }


    }

    post {

        always {

            junit '**/target/surefire-reports/*.xml'
            junit '**/target/failsafe-reports/*.xml'

            publishHTML([
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'JaCoCo Coverage Report',
                keepAll: true,
                alwaysLinkToLastBuild: true,
                allowMissing: false
            ])

        }
    }
}