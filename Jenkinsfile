String version = ''
String gitCommit = ''
String branchName = ''
String projectName = 'ontrack'

boolean pr = false

pipeline {

    agent none

    options {
        // General Jenkins job properties
        buildDiscarder(logRotator(numToKeepStr: '40'))
        // Timestamps
        timestamps()
        // No durability
        durabilityHint('PERFORMANCE_OPTIMIZED')
    }

    stages {

        stage('Setup') {
            agent {
                dockerfile {
                    label "docker"
                    args "--volume /var/run/docker.sock:/var/run/docker.sock"
                }
            }
            steps {
                script {
                    branchName = ontrackBranchName(BRANCH_NAME)
                    echo "Ontrack branch name = ${branchName}"
                    pr = BRANCH_NAME ==~ 'PR-.*'
                }
                script {
                    if (pr) {
                        echo "No Ontrack setup for PR."
                    } else {
                        echo "Ontrack setup for ${branchName}"
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
            }
        }

        stage('Build') {
            agent {
                dockerfile {
                    label "docker"
                    args "--volume /var/run/docker.sock:/var/run/docker.sock"
                }
            }
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
    publishToMavenLocal \\
    osPackages \\
    dockerLatest \\
    -Pdocumentation \\
    -PbowerOptions='--allow-root' \\
    -Dorg.gradle.jvmargs=-Xmx2048m \\
    --stacktrace \\
    --profile \\
    --parallel \\
    --console plain
'''
                script {
                    // Reads version information
                    def props = readProperties(file: 'build/version.properties')
                    version = props.VERSION_DISPLAY
                    gitCommit = props.VERSION_COMMIT
                }
                echo "Version = ${version}"
                sh """\
echo "(*) Building the test extension..."
cd ontrack-extension-test
./gradlew \\
    clean \\
    build \\
    -PontrackVersion=${version} \\
    -PbowerOptions='--allow-root' \\
    -Dorg.gradle.jvmargs=-Xmx2048m \\
    --stacktrace \\
    --profile \\
    --console plain
"""
            }
            post {
                always {
                    junit '**/build/test-results/**/*.xml'
                }
                success {
                    script {
                        if (!pr) {
                            ontrackBuild(project: projectName, branch: branchName, build: version, gitCommit: gitCommit)
                        }
                    }
                    stash name: "delivery", includes: "build/distributions/ontrack-*-delivery.zip"
                    stash name: "rpm", includes: "build/distributions/*.rpm"
                    stash name: "debian", includes: "build/distributions/*.deb"
                    archiveArtifacts "build/distributions/ontrack-*-delivery.zip"
                }
            }
        }

        stage('Local acceptance tests') {
            agent {
                dockerfile {
                    label "docker"
                    args "--volume /var/run/docker.sock:/var/run/docker.sock"
                }
            }
            steps {
                // Runs the acceptance tests
                timeout(time: 25, unit: 'MINUTES') {
                    sh """\
#!/bin/bash
set -e
echo "Launching tests..."
cd ontrack-acceptance/src/main/compose
docker-compose --project-name local up --exit-code-from ontrack_acceptance
"""
                }
            }
            post {
                always {
                    sh """\
#!/bin/bash
set -e
echo "Cleanup..."
mkdir -p build
cp -r ontrack-acceptance/src/main/compose/build build/acceptance
cd ontrack-acceptance/src/main/compose
docker-compose --project-name local down --volumes
"""
                    archiveArtifacts 'build/acceptance/**'
                    junit 'build/acceptance/*.xml'
                    script {
                        if (!pr) {
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
            }
        }

        // We stop here for pull requests and feature branches

        // Docker push
        stage('Docker publication') {
            agent {
                dockerfile {
                    label "docker"
                    args "--volume /var/run/docker.sock:/var/run/docker.sock"
                }
            }
            when {
                branch 'release/*'
            }
            environment {
                DOCKER_HUB = credentials("DOCKER_HUB")
                ONTRACK_VERSION = "${version}"
            }
            steps {
                script {
                    sh '''\
#!/bin/bash
set -e
docker login --username ${DOCKER_HUB_USR} --password ${DOCKER_HUB_PSW}
docker push nemerosa/ontrack:${ONTRACK_VERSION}
docker push nemerosa/ontrack-acceptance:${ONTRACK_VERSION}
docker push nemerosa/ontrack-extension-test:${ONTRACK_VERSION}
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
            environment {
                ONTRACK_VERSION = "${version}"
            }
            when {
                branch 'release/*'
            }
            parallel {
                // CentOS7
                stage('CentOS7') {
                    agent {
                        dockerfile {
                            label "docker"
                            args "--volume /var/run/docker.sock:/var/run/docker.sock"
                        }
                    }
                    steps {
                        unstash name: "rpm"
                        timeout(time: 25, unit: 'MINUTES') {
                            sh """\
#!/bin/bash
set -e

echo "Preparing environment..."
DOCKER_DIR=ontrack-acceptance/src/main/compose/os/centos/7/docker
rm -f \${DOCKER_DIR}/*.rpm
cp build/distributions/*rpm \${DOCKER_DIR}/ontrack.rpm

echo "Launching test environment..."
cd ontrack-acceptance/src/main/compose
docker-compose --project-name centos --file docker-compose-centos-7.yml up --build -d ontrack

echo "Launching Ontrack in CentOS environment..."
CONTAINER=`docker-compose --project-name centos --file docker-compose-centos-7.yml ps -q ontrack`
echo "... for container \${CONTAINER}"
docker container exec \${CONTAINER} /etc/init.d/ontrack start

echo "Launching tests..."
docker-compose --project-name centos --file docker-compose-centos-7.yml up --exit-code-from ontrack_acceptance ontrack_acceptance
"""
                        }
                    }
                    post {
                        always {
                            sh """\
#!/bin/bash
set -e
echo "Cleanup..."
mkdir -p build
cp -r ontrack-acceptance/src/main/compose/build build/centos
cd ontrack-acceptance/src/main/compose
docker-compose --project-name centos --file docker-compose-centos-7.yml down --volumes
"""
                            archiveArtifacts 'build/centos/**'
                            junit 'build/centos/*.xml'
                            ontrackValidate(
                                    project: projectName,
                                    branch: branchName,
                                    build: version,
                                    validationStamp: 'ACCEPTANCE.CENTOS.7',
                                    buildResult: currentBuild.result,
                            )
                        }
                    }
                }
                // Debian
                stage('Debian') {
                    agent {
                        dockerfile {
                            label "docker"
                            args "--volume /var/run/docker.sock:/var/run/docker.sock"
                        }
                    }
                    steps {
                        unstash name: "debian"
                        timeout(time: 25, unit: 'MINUTES') {
                            sh """\
#!/bin/bash
set -e

echo "Preparing environment..."
DOCKER_DIR=ontrack-acceptance/src/main/compose/os/debian/docker
rm -f \${DOCKER_DIR}/*.deb
cp build/distributions/*.deb \${DOCKER_DIR}/ontrack.deb

echo "Launching test environment..."
cd ontrack-acceptance/src/main/compose
docker-compose --project-name debian --file docker-compose-debian.yml up --build -d ontrack

echo "Launching Ontrack in Debian environment..."
CONTAINER=`docker-compose --project-name debian --file docker-compose-debian.yml ps -q ontrack`
echo "... for container \${CONTAINER}"
docker container exec \${CONTAINER} /etc/init.d/ontrack start

echo "Launching tests..."
docker-compose --project-name debian --file docker-compose-debian.yml up --build --exit-code-from ontrack_acceptance ontrack_acceptance
"""
                        }
                    }
                    post {
                        always {
                            sh """\
#!/bin/bash
set -e
echo "Cleanup..."
mkdir -p build/debian
cp -r ontrack-acceptance/src/main/compose/build/* build/debian/
cd ontrack-acceptance/src/main/compose
docker-compose --project-name debian --file docker-compose-debian.yml down --volumes
"""
                            archiveArtifacts 'build/debian/**'
                            junit 'build/debian/*.xml'
                            ontrackValidate(
                                    project: projectName,
                                    branch: branchName,
                                    build: version,
                                    validationStamp: 'ACCEPTANCE.DEBIAN',
                                    buildResult: currentBuild.result,
                            )
                        }
                    }
                }
                // Extension tests
                stage('Local extension tests') {
                    agent {
                        dockerfile {
                            label "docker"
                            args "--volume /var/run/docker.sock:/var/run/docker.sock"
                        }
                    }
                    steps {
                        timeout(time: 25, unit: 'MINUTES') {
                            // Cleanup
                            sh """\
rm -rf ontrack-acceptance/src/main/compose/build
"""
                            // Launches the tests
                            sh """\
#!/bin/bash
set -e
echo "Launching tests..."
cd ontrack-acceptance/src/main/compose
docker-compose --project-name ext --file docker-compose-ext.yml up --exit-code-from ontrack_acceptance
"""
                        }
                    }
                    post {
                        always {
                            sh """\
echo "Cleanup..."
mkdir -p build
rm -rf build/extension
cp -r ontrack-acceptance/src/main/compose/build build/extension
cd ontrack-acceptance/src/main/compose
docker-compose --project-name ext --file docker-compose-ext.yml down --volumes
"""
                            archiveArtifacts 'build/extension/**'
                            junit 'build/extension/*.xml'
                            ontrackValidate(
                                    project: projectName,
                                    branch: branchName,
                                    build: version,
                                    validationStamp: 'EXTENSIONS',
                                    buildResult: currentBuild.result,
                            )
                        }
                    }
                }
                // Digital Ocean
                stage('Digital Ocean') {
                    agent {
                        dockerfile {
                            label "docker"
                            args "--volume /var/run/docker.sock:/var/run/docker.sock"
                        }
                    }
                    environment {
                        ONTRACK_VERSION = "${version}"
                        DROPLET_NAME = "ontrack-acceptance-${version}"
                        DO_TOKEN = credentials("DO_NEMEROSA_JENKINS2_BUILD")
                    }
                    steps {
                        timeout(time: 60, unit: 'MINUTES') {
                            sh '''\
#!/bin/bash

echo "(*) Cleanup..."
rm -rf ontrack-acceptance/src/main/compose/build

echo "(*) Removing any previous machine: ${DROPLET_NAME}..."
docker-machine rm --force ${DROPLET_NAME} > /dev/null

# Failing on first error from now on
set -e

echo "(*) Creating ${DROPLET_NAME} droplet..."
docker-machine create \\
    --driver=digitalocean \\
    --digitalocean-access-token=${DO_TOKEN} \\
    --digitalocean-image=docker \\
    --digitalocean-region=fra1 \\
    --digitalocean-size=1gb \\
    --digitalocean-backups=false \\
    ${DROPLET_NAME}

echo "(*) Gets ${DROPLET_NAME} droplet IP..."
DROPLET_IP=`docker-machine ip ${DROPLET_NAME}`
echo "Droplet IP = ${DROPLET_IP}"

echo "(*) Target Ontrack application..."
export ONTRACK_ACCEPTANCE_TARGET_URL="http://${DROPLET_IP}:8080"

echo "(*) Launching the remote Ontrack ecosystem..."
eval $(docker-machine env --shell bash ${DROPLET_NAME})
docker-compose \\
    --file ontrack-acceptance/src/main/compose/docker-compose-do-server.yml \\
    --project-name ontrack \\
    up -d

echo "(*) Running the tests..."
eval $(docker-machine env --shell bash --unset)
docker-compose \\
    --file ontrack-acceptance/src/main/compose/docker-compose-do-client.yml \\
    --project-name do \\
    up --exit-code-from ontrack_acceptance

'''
                        }
                    }
                    post {
                        always {
                            sh '''\
#!/bin/bash

echo "(*) Copying the test results..."
mkdir -p build
rm -rf build/do
cp -r ontrack-acceptance/src/main/compose/build build/do

echo "(*) Removing the test environment..."
docker-compose \\
    --file ontrack-acceptance/src/main/compose/docker-compose-do-client.yml \\
    --project-name do \\
    down

echo "(*) Removing any previous machine: ${DROPLET_NAME}..."
docker-machine rm --force ${DROPLET_NAME}
'''
                            archiveArtifacts 'build/do/**'
                            junit 'build/do/*.xml'
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

        // Publication

        stage('Publication') {
            when {
                branch 'release/*'
            }
            environment {
                ONTRACK_VERSION = "${version}"
            }
            parallel {
                stage('Docker push') {
                    agent {
                        dockerfile {
                            label "docker"
                            args "--volume /var/run/docker.sock:/var/run/docker.sock"
                        }
                    }
                    environment {
                        DOCKER_HUB = credentials("DOCKER_HUB")
                    }
                    steps {
                        echo "Docker push"
                        sh '''\
#!/bin/bash
set -e

docker login --username ${DOCKER_HUB_USR} --password ${DOCKER_HUB_PSW}

docker pull nemerosa/ontrack:${ONTRACK_VERSION}

docker tag nemerosa/ontrack:${ONTRACK_VERSION} nemerosa/ontrack:2
docker tag nemerosa/ontrack:${ONTRACK_VERSION} nemerosa/ontrack:latest

docker push nemerosa/ontrack:2
docker push nemerosa/ontrack:latest
'''
                    }
                }
                stage('Maven publication') {
                    agent {
                        dockerfile {
                            label "docker"
                            args "--volume /var/run/docker.sock:/var/run/docker.sock"
                        }
                    }
                    environment {
                        ONTRACK_COMMIT = "${gitCommit}"
                        ONTRACK_BRANCH = "${branchName}"
                        GPG_KEY = credentials("GPG_KEY")
                        GPG_KEY_RING = credentials("GPG_KEY_RING")
                        OSSRH = credentials("OSSRH")
                    }
                    steps {
                        echo "Maven publication"

                        unstash name: "delivery"
                        sh '''\
#!/bin/bash
set -e
unzip -n build/distributions/ontrack-${ONTRACK_VERSION}-delivery.zip -d ${WORKSPACE}
unzip -n ${WORKSPACE}/ontrack-publication.zip -d publication
'''

                        sh '''\
#!/bin/bash
set -e

./gradlew \\
    --build-file publication.gradle \\
    --info \\
    --profile \\
    --console plain \\
    --stacktrace \\
    -PontrackVersion=${ONTRACK_VERSION} \\
    -PontrackVersionCommit=${ONTRACK_COMMIT} \\
    -PontrackReleaseBranch=${ONTRACK_BRANCH} \\
    -Psigning.keyId=${GPG_KEY_USR} \\
    -Psigning.password=${GPG_KEY_PSW} \\
    -Psigning.secretKeyRingFile=${GPG_KEY_RING} \\
    -PossrhUser=${OSSRH_USR} \\
    -PossrhPassword=${OSSRH_PSW} \\
    publicationMaven
'''
                    }
                }
            }
        }

        // Release

        stage('Release') {
            agent {
                dockerfile {
                    label "docker"
                    args "--volume /var/run/docker.sock:/var/run/docker.sock"
                }
            }
            environment {
                ONTRACK_VERSION = "${version}"
                ONTRACK_COMMIT = "${gitCommit}"
                ONTRACK_BRANCH = "${branchName}"
                GITHUB = credentials("GITHUB_NEMEROSA_JENKINS2")
            }
            when {
                branch 'release/*'
            }
            steps {
                echo "Release"

                unstash name: "delivery"
                unstash name: "rpm"
                unstash name: "debian"
                sh '''\
#!/bin/bash
set -e
unzip -n build/distributions/ontrack-${ONTRACK_VERSION}-delivery.zip -d ${WORKSPACE}
unzip -n ${WORKSPACE}/ontrack-publication.zip -d publication
'''

                sh '''\
#!/bin/bash
set -e

./gradlew \\
    --build-file publication.gradle \\
    --info \\
    --profile \\
    --console plain \\
    --stacktrace \\
    -PontrackVersion=${ONTRACK_VERSION} \\
    -PontrackVersionCommit=${ONTRACK_COMMIT} \\
    -PontrackReleaseBranch=${ONTRACK_BRANCH} \\
    -PgitHubUser=${GITHUB_USR} \\
    -PgitHubPassword=${GITHUB_PSW} \\
    publicationRelease
'''

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

        // Site

        stage('Site') {
            agent {
                dockerfile {
                    label "docker"
                    args "--volume /var/run/docker.sock:/var/run/docker.sock"
                }
            }
            environment {
                ONTRACK_VERSION = "${version}"
                GITHUB = credentials("GITHUB_NEMEROSA_JENKINS2")
            }
            when {
                branch 'release/*'
            }
            steps {
                echo "Release"

                unstash name: "delivery"
                sh '''\
#!/bin/bash
set -e
unzip -n build/distributions/ontrack-${ONTRACK_VERSION}-delivery.zip -d ${WORKSPACE}
unzip -n ${WORKSPACE}/ontrack-publication.zip -d publication
'''

                sh '''\
#!/bin/bash
set -e

GITHUB_URI=`git config remote.origin.url`

./gradlew \\
    --build-file site.gradle \\
    --info \\
    --profile \\
    --console plain \\
    --stacktrace \\
    -PontrackVersion=${ONTRACK_VERSION} \\
    -PontrackGitHubUri=${GITHUB_URI} \\
    -PontrackGitHubPages=gh-pages \\
    -PontrackGitHubUser=${GITHUB_USR} \\
    -PontrackGitHubPassword=${GITHUB_PSW} \\
    site
'''

            }
            post {
                success {
                    ontrackValidate(
                            project: projectName,
                            branch: branchName,
                            build: version,
                            validationStamp: 'SITE',
                            buildResult: currentBuild.result,
                    )
                }
            }
        }

    }

}
