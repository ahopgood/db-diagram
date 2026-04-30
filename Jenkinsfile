pipeline {
    agent { label 'Java17 && Graphviz' }
    stages {
        stage('build') {
            steps {
                sh '''
                # SHORT TERM FIX due to build image issues
                # GPG is required for the package signing key
                sudo apt install gpg
                
                # Download the signing key to a new keyring
                wget -O- https://apt.releases.hashicorp.com/gpg | gpg --dearmor | sudo tee /usr/share/keyrings/hashicorp-archive-keyring.gpg
                
                # Verify the key's fingerprint
                gpg --no-default-keyring --keyring /usr/share/keyrings/hashicorp-archive-keyring.gpg --fingerprint
                
                # The fingerprint must match 798A EC65 4E5C 1542 8C8E 42EE AA16 FCBC A621 E701, which can also be verified at https://www.hashicorp.com/security under "Linux Package Checksum Verification".
                
                # Add the HashiCorp repo
                echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/hashicorp.list
                '''
                sh 'apt update && apt install -y libncurses5'
                sh 'mvn --version'
                sh 'mvn clean install'
            }
        }
    }
    post {
        always {
            junit 'target/surefire-reports/*.xml'
            jacoco(
                  execPattern: 'target/jacoco.exec',
                  classPattern: 'target/classes',
                  sourcePattern: 'src/main/java',
                  exclusionPattern: 'src/test*'
            )
            recordIssues enabledForFailure: true, tool: spotBugs(pattern: '**/target/spotbugsXml.xml')
            recordIssues enabledForFailure: true, tool: pmdParser(pattern: '**/target/pmd.xml')
            recordIssues enabledForFailure: true, tool: checkStyle(pattern: '**/target/checkstyle-result.xml')
            dependencyCheckPublisher pattern: '**/target/dependency-check-report.xml'
            publishHTML (target: [
                allowMissing: false,
                alwaysLinkToLastBuild: false,
                keepAll: true,
                reportDir: 'target/site/',
                reportFiles: 'dependency-updates-report.html,property-updates-report.html,plugin-updates-report.html',
                reportName: "Versions Report"
            ])
        }
    }
}
