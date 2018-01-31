String version = ''
String gitCommit = ''
String branchName = ''
String projectName = 'ontrack'

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

        stage('Setup') {
            steps {
                script {
                    branchName = ontrackBranchName(BRANCH_NAME)
                    echo "Ontrack branch name = ${branchName}"
                }
                ontrackBranchSetup(project: projectName, branch: branchName, script: """
                    branch.config {
                        gitBranch '${branchName}', [
                            buildCommitLink: [
                                id: 'git-commit-property'
                            ]
                        ]
                    }
                """)
            }
        }

        stage('Build') {
            steps {
                sh '''\
git checkout -B ${BRANCH_NAME}
git clean -xfd
'''
                sh '''\
./gradlew \\
    clean \\
    versionDisplay \\
    versionFile \\
    test \\
    build \\
    integrationTest \\
    dockerLatest \\
    -Pdocumentation \\
    -PbowerOptions='--allow-root' \\
    -Dorg.gradle.jvmargs=-Xmx1536m \\
    --stacktrace \\
    --profile \\
    --console plain
'''
                script {
                    // Reads version information
                    def props = readProperties(file: 'build/version.properties')
                    version = props.VERSION_DISPLAY
                    gitCommit = props.VERSION_COMMIT
                }
            }
            post {
                success {
                    ontrackBuild(project: projectName, branch: branchName, build: version, gitCommit: gitCommit)
                }
            }
        }

        stage('Local acceptance tests') {
           steps {
             // Runs the acceptance tests
               timeout(time: 25, unit: 'MINUTES') {
                   sh """\
echo "Launching environment..."
cd ontrack-acceptance/src/main/compose
docker-compose up -d ontrack selenium
"""
                   sh """\
echo "Launching tests..."
cd ontrack-acceptance/src/main/compose
docker-compose up ontrack_acceptance
"""
               }
          }
          post {
             always {
                sh """\
echo "Cleanup..."
cd ontrack-acceptance/src/main/compose
docker-compose down --volumes
"""
                 archiveArtifacts 'ontrack-acceptance/src/main/compose/build/**'
                 junit 'ontrack-acceptance/src/main/compose/build/*.xml'
                 ontrackValidate(
                         project: projectName,
                         branch: branchName,
                         build: version,
                         validationStamp: 'ACCEPTANCE',
                         buildResult: currentBuild.result,
                 )
             }
          }
        }

        // Docker push
        stage('Docker publication') {
            environment {
                DOCKER_HUB = credentials("DOCKER_HUB")
                ONTRACK_VERSION = "${version}"
            }
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    input "Pushing version ${version} to the Docker Hub?"
                }
                script {
                    sh '''\
#!/bin/bash
set -e
docker login --username ${DOCKER_HUB_USR} --password ${DOCKER_HUB_PSW}
docker push nemerosa/ontrack:${ONTRACK_VERSION}
'''
                }
            }
            post {
                always {
                    ontrackValidate(
                            project: projectName,
                            branch: branchName,
                            build: version,
                            validationStamp: 'DOCKER',
                            buildResult: currentBuild.result,
                    )
                }
            }
        }

        // OS tests + DO tests in parallel

        stage('Platform tests') {
            parallel {
                // TODO CentOS7
                stage('CentOS7') {
                    when {
                        branch 'release/.*'
                    }
                    steps {
                        ontrackValidate(
                                project: projectName,
                                branch: branchName,
                                build: version,
                                validationStamp: 'ACCEPTANCE.CENTOS.7',
                                buildResult: currentBuild.result,
                        )
                    }
                }
                // TODO Debian
                stage('Debian') {
                    when {
                        branch 'release/.*'
                    }
                    steps {
                        ontrackValidate(
                                project: projectName,
                                branch: branchName,
                                build: version,
                                validationStamp: 'ACCEPTANCE.DEBIAN',
                                buildResult: currentBuild.result,
                        )
                    }
                }
                // Digital Ocean
                stage('Digital Ocean') {
                    environment {
                        ONTRACK_VERSION = "${version}"
                        DROPLET_NAME = "ontrack-acceptance-${version}"
                        // TODO DO token
                    }
                    steps {
                        timeout(time: 25, unit: 'MINUTES') {
                            sh '''\
#!/bin/bash

echo "Removing any previous machine: ${DROPLET_NAME}..."
docker-machine rm --force ${DROPLET_NAME}

echo "Creating ${DROPLET_NAME} droplet..."
docker-machine create \\
    --driver=digitalocean \\
    --digitalocean-access-token=${DO_TOKEN} \\
    --digitalocean-image=docker \\
    --digitalocean-region=fra1 \\
    --digitalocean-size=1gb \\
    --digitalocean-backups=false \\
    ${DROPLET_NAME}
if [ "$?" != "0" ]
then
    echo "Cannot create droplet ${DROPLET_NAME}."
    exit 1
fi

echo "Gets ${DROPLET_NAME} droplet IP..."
DROPLET_IP=`docker-machine ip ${DROPLET_NAME}`
echo "Droplet IP = ${DROPLET_IP}"
'''
                        }
                    }
                    post {
                        always {
                            sh '''\
#!/bin/bash
echo "Removing any previous machine: ${DROPLET_NAME}..."
docker-machine rm --force ${DROPLET_NAME}
'''
                            ontrackValidate(
                                    project: projectName,
                                    branch: branchName,
                                    build: version,
                                    validationStamp: 'ACCEPTANCE.DO',
                                    buildResult: currentBuild.result,
                            )
                        }
                    }
                }
            }
        }

        /*

        stage('Release') {
            steps {
                echo "Releasing..."
                // TODO Release
            }
            post {
                success {
                    ontrackPromote(
                            project: projectName,
                            branch: branchName,
                            build: version,
                            promotionLevel: 'RELEASE',
                    )
                }
            }
        }

        */

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
