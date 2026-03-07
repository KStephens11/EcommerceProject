pipeline {

    agent any

    tools {
            jdk 'Java25'
            maven 'maven'
        }

    stages {

        stage('Checkout') {
            steps {
                sh 'java -version'
                sh 'javac -version'
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean test package'
            }
        }

    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
    }


}