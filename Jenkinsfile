String version = ''
String gitCommit = ''
String branchName = ''

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
                ontrackBranchSetup(project: 'ontrack', branch: branchName, script: """
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
                    ontrackBuild(project: 'ontrack', branch: branchName, build: version, gitCommit: gitCommit)
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
                 archive 'ontrack-acceptance/src/main/compose/build'
                 // TODO junit 'delivery/ontrack-acceptance/build/acceptance/*.xml'
                 ontrackValidate(
                         project: 'ontrack',
                         branch: branchName,
                         build: version,
                         validationStamp: 'ACCEPTANCE',
                         buildResult: currentBuild.result,
                 )
             }
          }
        }

        /*

        // Docker push
        stage('Docker publication') {
            steps {
                input "Pushing version ${version} to the Docker Hub?"
                script {
                    docker.withRegistry('https://index.docker.io/v2', 'DOCKER_HUB') {
                        def image = docker.image("nemerosa/ontrack:${version}")
                        image.push()
                        // TODO If 2.x, pushes `latest` as well
                    }
                }
            }
            post {
                always {
                    ontrackValidate(
                            project: 'ontrack',
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
                    steps {
                        ontrackValidate(
                                project: 'ontrack',
                                branch: branchName,
                                build: version,
                                validationStamp: 'ACCEPTANCE.CENTOS.7',
                                buildResult: currentBuild.result,
                        )
                    }
                }
                // TODO Debian
                stage('Debian') {
                    steps {
                        ontrackValidate(
                                project: 'ontrack',
                                branch: branchName,
                                build: version,
                                validationStamp: 'ACCEPTANCE.DEBIAN',
                                buildResult: currentBuild.result,
                        )
                    }
                }
                // TODO Digital Ocean
                stage('Digital Ocean') {
                    steps {
                        ontrackValidate(
                                project: 'ontrack',
                                branch: branchName,
                                build: version,
                                validationStamp: 'ACCEPTANCE.DO',
                                buildResult: currentBuild.result,
                        )
                    }
                }
            }
        }

        stage('Release') {
            steps {
                echo "Releasing..."
                // TODO Release
            }
            post {
                success {
                    ontrackPromote(
                            project: 'ontrack',
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
