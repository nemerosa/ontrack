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
git checkout -B ${BRANCH_NAME}
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

        stage('Local acceptance tests') {
           steps {
             // Gets the delivery zip
             dir('delivery') {
                unstash 'delivery-zip'
             }
             // Unzips the delivery
             sh '''\
unzip delivery/build/distributions/*-delivery.zip -d delivery
unzip delivery/ontrack-publication.zip -d publication
'''
             // Runs the acceptance tests
             // TODO Docker host problem (-PciHost=dockerhost)
             sh '''\
./gradlew \\
   ciAcceptanceTest \\
   -PacceptanceJar=publication/ontrack-acceptance-*.jar \\
   -Dorg.gradle.jvmargs=-Xmx1536m \\
   --info \\
   --profile \\
   --console plain \\
   --stacktrace
             '''
          }
        }
        // TODO Ontrack validation --> ACCEPTANCE
        // TODO Docker push
        // TODO Ontrack validation --> DOCKER
        // TODO OS tests + DO tests in parallel
        // TODO Ontrack validation --> ACCEPTANCE.DEBIAN
        // TODO Ontrack validation --> ACCEPTANCE.CENTOS.6
        // TODO Ontrack validation --> ACCEPTANCE.CENTOS.7
        // TODO Ontrack validation --> ACCEPTANCE.DO
        // TODO Release
        // TODO Ontrack promotion --> RELEASE
        // TODO Site
        // TODO Ontrack validation --> SITE
        // TODO Production
        // TODO Ontrack promotion --> ONTRACK
        // TODO Production tests
        // TODO Ontrack validation --> ACCEPTANCE.PRODUCTION

    }

    post {
        always {
            junit '**/build/test-results/**/*.xml'
        }
    }

}
