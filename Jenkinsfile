pipeline {
    agent { label 'Java11' }
    stages {
        stage('build') {
            steps {
                git credentialsId: 'github_token', url: 'https://github.com/ahopgood/db-diagram.git', branch: '${BRANCH_NAME}'
                sh 'mvn --version'
                sh 'mvn clean install'
            }
        }
    }
    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
    }
}
