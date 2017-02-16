pipeline {

    agent {
        dockerfile {
            label "docker"
        }
    }

    stages {

        stage('Build') {
            steps {
                sh '''\
./gradlew
    clean
    versionDisplay
    versionFile
    test
    build
    -Pdocumentation
    -PbowerOptions='--allow-root\'
    -Dorg.gradle.jvmargs=-Xmx1536m
    --info
    --stacktrace
    --profile
    --console plain
'''
            }
        }

        // TODO Integration tests
        // TODO Docker image
        // TODO OS packages

    }

    post {
        always {
            junit '**/build/test-results/**/*.xml'
        }
    }

}