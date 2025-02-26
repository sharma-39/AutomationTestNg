pipeline {
    agent any

    stages {

      stage('Build') {
            steps {
                script {
                    sh 'mvn clean install'
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    sh 'mvn test -Dsurefire.suiteXmlFiles=testng.xml'
                }
            }
        }

        stage('Post-Build Actions') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }
    }
}
