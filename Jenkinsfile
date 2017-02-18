pipeline {

    agent {
        dockerfile {
            label "docker"
            args "--volume /var/run/docker.sock:/var/run/docker.sock"
        }
    }

    options {
        // General Jenkins job properties
        buildDiscarder(logRotator(numToKeepStr: '40'))
        // Timestamps
        timestamps()
    }

    stages {

        stage('Build') {
            steps {
                sh '''\
git checkout -b ${BRANCH_NAME}
'''
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

        stage('Docker image') {
            steps {
                sh '''\
./gradlew \\
    dockerLatest \\
    -Dorg.gradle.jvmargs=-Xmx1536m \\
    --info \\
    --stacktrace \\
    --profile \\
    --console plain
'''
            }
        }

        stage('OS packages') {
            steps {
                sh '''\
./gradlew \\
    osPackages \\
    -Dorg.gradle.jvmargs=-Xmx1536m \\
    --info \\
    --stacktrace \\
    --profile \\
    --console plain
'''
               // Saving the delivery ZIP for later
               stash includes: 'build/distributions/*-delivery.zip', name: 'delivery-zip'
            }
        }

        // TODO Ontrack build
        // TODO Local acceptance tests
        // TODO Docker push
        // TODO OS tests + DO tests in parallel
        // TODO Release
        // TODO Site
        // TODO Production
        // TODO Production tests

    }

    post {
        always {
            junit '**/build/test-results/**/*.xml'
        }
    }

}
