pipeline {

    agent {
        dockerfile {
            label "docker"
        }
    }

    options {
        // General Jenkins job properties
        buildDiscarder(logRotator(numToKeepStr: '40'))
    }

    stages {

        stage('Build') {
            steps {
                sh '''\
./gradlew \\
    clean \\
    versionDisplay \\
    versionFile \\
    test \\
    build \\
    -Pdocumentation \\
    -PbowerOptions='--allow-root' \\
    -Dorg.gradle.jvmargs=-Xmx1536m \\
    --info \\
    --stacktrace \\
    --profile \\
    --console plain
'''
            }
        }

        stage('Integration tests') {
            agent {
                reuseNode true
            }
            steps {
                sh '''\
./gradlew \\
    integrationTest \\
    -Dorg.gradle.jvmargs=-Xmx1536m \\
    --info \\
    --stacktrace \\
    --profile \\
    --console plain
'''
            }
        }

        // TODO Docker image
        // TODO OS packages

    }

    post {
        always {
            junit '**/build/test-results/**/*.xml'
        }
    }

}