pipeline {
    agent any

    environment {
        MAVEN_HOME = 'D:\\Sharma\\Office\\Office\apache-maven-3.8.3' // Adjust based on your system
    }

    stages {
        stage('Checkout Code') {
            steps {
                git url: 'https://github.com/sharma-39/AutomationTestNg.git', branch: 'main'
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean test' // Use 'bat' instead of 'sh' for Windows
            }
        }

        stage('Publish Test Results') {
            steps {
                publishTestNG()
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
