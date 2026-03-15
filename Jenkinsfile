pipeline {

    agent any

    tools {
        jdk 'Java25'
        maven 'maven'
    }

    parameters {
        booleanParam(
            name: 'RUN_UI_TESTS',
            defaultValue: false,
            description: 'Run Karate/Selenium tests'
        )
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
            script {
              if (!params.RUN_UI_TESTS) {
                bat "mvn -B clean verify -DskipITs=true"
              } else {
                bat "mvn -B clean verify -DskipITs=false"
              }
            }
          }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                      mvn sonar:sonar \
                        -Dsonar.projectKey=EcommerceProject
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
            archiveArtifacts artifacts: '**/target/*.jar, **/target/*.war', allowEmptyArchive: true

            publishHTML([
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'JaCoCo Coverage Report',
                keepAll: true,
                alwaysLinkToLastBuild: true,
                allowMissing: true
            ])

            publishHTML([
                reportDir: 'target/karate-reports', // directory where karate-summary.html is
                reportFiles: 'karate-summary.html',      // main HTML file
                reportName: 'Karate Report',
                keepAll: true,
                alwaysLinkToLastBuild: true,
                allowMissing: true
            ])

        }
    }
}