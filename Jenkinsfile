String version = ''
String gitCommit = ''
String branchName = ''
String projectName = 'ontrack'

@Library("ontrack-jenkins-library@1.0.0") _

boolean pr = false

String buildImageVersion = "nemerosa/ontrack-build:1.0.1"

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

    environment {
        DOCKER_REGISTRY_CREDENTIALS = credentials("DOCKER_NEMEROSA")
    }

    stages {

        stage('Setup') {
            agent {
                docker {
                    image buildImageVersion
                }
            }
            when {
                beforeAgent true
                not {
                    branch 'master'
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
                                gitBranch '${BRANCH_NAME}', [
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
                docker {
                    image buildImageVersion
                    args "--volume /var/run/docker.sock:/var/run/docker.sock --network host"
                }
            }
            when {
                beforeAgent true
                not {
                    branch 'master'
                }
            }
            environment {
                CODECOV_TOKEN = credentials("CODECOV_TOKEN")
            }
            steps {
                sh '''\
git checkout -B ${BRANCH_NAME}
git clean -xfd
'''
                sh ''' ./gradlew clean versionDisplay versionFile'''
                script {
                    // Reads version information
                    def props = readProperties(file: 'build/version.properties')
                    version = props.VERSION_DISPLAY
                    gitCommit = props.VERSION_COMMIT
                    // If not a PR, create a build
                    if (!pr) {
                        ontrackBuild(project: projectName, branch: branchName, build: version, gitCommit: gitCommit)
                    }
                }
                echo "Version = ${version}"
                sh '''\
./gradlew \\
    test \\
    build \\
    integrationTest \\
    codeCoverageReport \\
    publishToMavenLocal \\
    osPackages \\
    dockerBuild \\
    -Pdocumentation \\
    -PbowerOptions='--allow-root' \\
    -Dorg.gradle.jvmargs=-Xmx4096m \\
    --stacktrace \\
    --profile \\
    --parallel \\
    --console plain
'''
                sh ''' curl -s https://codecov.io/bash | bash -s -- -c -F build'''
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
                echo "Pushing image to registry..."
                sh """\
echo \${DOCKER_REGISTRY_CREDENTIALS_PSW} | docker login docker.nemerosa.net --username \${DOCKER_REGISTRY_CREDENTIALS_USR} --password-stdin

docker tag nemerosa/ontrack:${version} docker.nemerosa.net/nemerosa/ontrack:${version}
docker tag nemerosa/ontrack-acceptance:${version} docker.nemerosa.net/nemerosa/ontrack-acceptance:${version}
docker tag nemerosa/ontrack-extension-test:${version} docker.nemerosa.net/nemerosa/ontrack-extension-test:${version}

docker push docker.nemerosa.net/nemerosa/ontrack:${version}
docker push docker.nemerosa.net/nemerosa/ontrack-acceptance:${version}
docker push docker.nemerosa.net/nemerosa/ontrack-extension-test:${version}
"""
            }
            post {
                always {
                    script {
                        def results = junit '**/build/test-results/**/*.xml'
                        // If not a PR, create a build validation stamp
                        if (!pr) {
                            ontrackValidate(
                                    project: projectName,
                                    branch: branchName,
                                    build: version,
                                    validationStamp: 'BUILD',
                                    testResults: results,
                            )
                        }
                    }
                }
                success {
                    stash name: "delivery", includes: "build/distributions/ontrack-*-delivery.zip"
                    stash name: "rpm", includes: "build/distributions/*.rpm"
                    stash name: "debian", includes: "build/distributions/*.deb"
                }
            }
        }

        stage('Local acceptance tests') {
            agent {
                docker {
                    image buildImageVersion
                    args "--volume /var/run/docker.sock:/var/run/docker.sock"
                }
            }
            when {
                beforeAgent true
                not {
                    branch 'master'
                }
            }
            environment {
                ONTRACK_VERSION = "${version}"
                CODECOV_TOKEN = credentials("CODECOV_TOKEN")
            }
            steps {
                // Runs the acceptance tests
                timeout(time: 25, unit: 'MINUTES') {
                    sh """\
#!/bin/bash
set -e

echo \${DOCKER_REGISTRY_CREDENTIALS_PSW} | docker login docker.nemerosa.net --username \${DOCKER_REGISTRY_CREDENTIALS_USR} --password-stdin

echo "Launching tests..."
cd ontrack-acceptance/src/main/compose
docker-compose --project-name local --file docker-compose.yml --file docker-compose-jacoco.yml up --exit-code-from ontrack_acceptance
"""
                }
            }
            post {
                success {
                    sh '''
                        #!/bin/bash
                        set -e
                        echo "Getting Jacoco coverage"
                        mkdir -p build/jacoco/
                        cp ontrack-acceptance/src/main/compose/jacoco/jacoco.exec build/jacoco/acceptance.exec
                        cp ontrack-acceptance/src/main/compose/jacoco-dsl/jacoco.exec build/jacoco/dsl.exec
                    '''
                    // Collection of coverage in Docker
                    sh '''
                        ./gradlew \\
                            codeDockerCoverageReport \\
                            -x classes \\
                            -PjacocoExecFile=build/jacoco/acceptance.exec \\
                            -PjacocoReportFile=build/reports/jacoco/acceptance.xml \\
                            --stacktrace \\
                            --profile \\
                            --console plain
                    '''
                    // Collection of coverage in DSL
                    sh '''
                        ./gradlew \\
                            codeDockerCoverageReport \\
                            -x classes \\
                            -PjacocoExecFile=build/jacoco/dsl.exec \\
                            -PjacocoReportFile=build/reports/jacoco/dsl.xml \\
                            --stacktrace \\
                            --profile \\
                            --console plain
                    '''
                    // Upload to Codecov
                    sh '''
                        curl -s https://codecov.io/bash | bash -s -- -c -F acceptance -f build/reports/jacoco/acceptance.xml
                        curl -s https://codecov.io/bash | bash -s -- -c -F dsl -f build/reports/jacoco/dsl.xml
                    '''
                }
                always {
                    sh '''
                        #!/bin/bash
                        set -e
                        echo "Cleanup..."
                        rm -rf build/acceptance
                        mkdir -p build
                        cp -r ontrack-acceptance/src/main/compose/build build/acceptance
                        cd ontrack-acceptance/src/main/compose
                        docker-compose --project-name local --file docker-compose.yml --file docker-compose-jacoco.yml down --volumes
                    '''
                    script {
                        def results = junit('build/acceptance/*.xml')
                        if (!pr) {
                            ontrackValidate(
                                    project: projectName,
                                    branch: branchName,
                                    build: version,
                                    validationStamp: 'ACCEPTANCE',
                                    testResults: results,
                            )
                        }
                    }
                }
            }
        }

        stage('Local extension tests') {
            when {
                not {
                    branch "master"
                }
                beforeAgent true
            }
            agent {
                docker {
                    image buildImageVersion
                    args "--volume /var/run/docker.sock:/var/run/docker.sock"
                }
            }
            environment {
                ONTRACK_VERSION = "${version}"
                CODECOV_TOKEN = credentials("CODECOV_TOKEN")
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

echo \${DOCKER_REGISTRY_CREDENTIALS_PSW} | docker login docker.nemerosa.net --username \${DOCKER_REGISTRY_CREDENTIALS_USR} --password-stdin

echo "Launching tests..."
cd ontrack-acceptance/src/main/compose
docker-compose --project-name ext --file docker-compose-ext.yml --file docker-compose-jacoco.yml up --exit-code-from ontrack_acceptance
"""
                }
            }
            post {
                success {
                    sh '''
                        #!/bin/bash
                        set -e
                        echo "Getting Jacoco coverage"
                        mkdir -p build/jacoco/
                        cp ontrack-acceptance/src/main/compose/jacoco/jacoco.exec build/jacoco/extension.exec
                    '''
                    // Collection of coverage in Docker
                    sh '''
                        ./gradlew \\
                            codeDockerCoverageReport \\
                            -x classes \\
                            -PjacocoExecFile=build/jacoco/extension.exec \\
                            -PjacocoReportFile=build/reports/jacoco/extension.xml \\
                            --stacktrace \\
                            --profile \\
                            --console plain
                    '''
                    // Upload to Codecov
                    sh '''
                        curl -s https://codecov.io/bash | bash -s -- -c -F extension -f build/reports/jacoco/extension.xml
                    '''
                }
                always {
                    sh """\
echo "Cleanup..."
mkdir -p build
rm -rf build/extension
cp -r ontrack-acceptance/src/main/compose/build build/extension
cd ontrack-acceptance/src/main/compose
docker-compose --project-name ext --file docker-compose-ext.yml --file docker-compose-jacoco.yml down --volumes
"""
                    script {
                        def results = junit 'build/extension/*.xml'
                        ontrackValidate(
                                project: projectName,
                                branch: branchName,
                                build: version,
                                validationStamp: 'EXTENSIONS',
                                testResults: results,
                        )
                    }
                }
            }
        }


        // We stop here for pull requests and feature branches

        // OS tests + DO tests in parallel

        stage('Platform tests') {
            environment {
                ONTRACK_VERSION = "${version}"
            }
            when {
                beforeAgent true
                branch 'release/*'
            }
            parallel {
                // CentOS7
                stage('CentOS7') {
                    agent {
                        docker {
                            image buildImageVersion
                            args "--volume /var/run/docker.sock:/var/run/docker.sock"
                        }
                    }
                    steps {
                        unstash name: "rpm"
                        timeout(time: 25, unit: 'MINUTES') {
                            sh """\
#!/bin/bash
set -e

echo \${DOCKER_REGISTRY_CREDENTIALS_PSW} | docker login docker.nemerosa.net --username \${DOCKER_REGISTRY_CREDENTIALS_USR} --password-stdin

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
                            script {
                                def results = junit 'build/centos/*.xml'
                                ontrackValidate(
                                        project: projectName,
                                        branch: branchName,
                                        build: version,
                                        validationStamp: 'ACCEPTANCE.CENTOS.7',
                                        testResults: results,
                                )
                            }
                        }
                    }
                }
                // Debian
                stage('Debian') {
                    agent {
                        docker {
                            image buildImageVersion
                            args "--volume /var/run/docker.sock:/var/run/docker.sock"
                        }
                    }
                    steps {
                        unstash name: "debian"
                        timeout(time: 25, unit: 'MINUTES') {
                            sh """\
#!/bin/bash
set -e

echo \${DOCKER_REGISTRY_CREDENTIALS_PSW} | docker login docker.nemerosa.net --username \${DOCKER_REGISTRY_CREDENTIALS_USR} --password-stdin

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
                            script {
                                def results = junit 'build/debian/*.xml'
                                ontrackValidate(
                                        project: projectName,
                                        branch: branchName,
                                        build: version,
                                        validationStamp: 'ACCEPTANCE.DEBIAN',
                                        testResults: results,
                                )
                            }
                        }
                    }
                }
                // Digital Ocean
                stage('Digital Ocean') {
                    agent {
                        docker {
                            image buildImageVersion
                            args "--volume /var/run/docker.sock:/var/run/docker.sock"
                        }
                    }
                    when {
                        // FIXME #683 Disabled until fixed
                        expression { false }
                    }
                    environment {
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
    --digitalocean-region=ams3 \\
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
echo ${DOCKER_REGISTRY_CREDENTIALS_PSW} | docker login docker.nemerosa.net --username ${DOCKER_REGISTRY_CREDENTIALS_USR} --password-stdin
docker-compose \\
    --file ontrack-acceptance/src/main/compose/docker-compose-do-server.yml \\
    --project-name ontrack \\
    up -d

echo "(*) Running the tests..."
eval $(docker-machine env --shell bash --unset)
echo ${DOCKER_REGISTRY_CREDENTIALS_PSW} | docker login docker.nemerosa.net --username ${DOCKER_REGISTRY_CREDENTIALS_USR} --password-stdin
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
                            script {
                                def results = junit 'build/do/*.xml'
                                ontrackValidate(
                                        project: projectName,
                                        branch: branchName,
                                        build: version,
                                        validationStamp: 'ACCEPTANCE.DO',
                                        testResults: results,
                                )
                            }
                        }
                    }
                }
            }
        }

        // Publication

        stage('Publication') {
            when {
                beforeAgent true
                branch 'release/*'
            }
            environment {
                ONTRACK_VERSION = "${version}"
            }
            parallel {
                stage('Docker Hub') {
                    agent {
                        docker {
                            image buildImageVersion
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

echo "Making sure the images are available on this node..."

echo ${DOCKER_REGISTRY_CREDENTIALS_PSW} | docker login docker.nemerosa.net --username ${DOCKER_REGISTRY_CREDENTIALS_USR} --password-stdin
docker image pull docker.nemerosa.net/nemerosa/ontrack:${ONTRACK_VERSION}

echo "Publishing in Docker Hub..."

echo ${DOCKER_HUB_PSW} | docker login --username ${DOCKER_HUB_USR} --password-stdin

docker image tag docker.nemerosa.net/nemerosa/ontrack:${ONTRACK_VERSION} nemerosa/ontrack:${ONTRACK_VERSION}

docker image push nemerosa/ontrack:${ONTRACK_VERSION}
'''
                    }
                    post {
                        always {
                            ontrackValidate(
                                    project: projectName,
                                    branch: branchName,
                                    build: version,
                                    validationStamp: 'DOCKER.HUB',
                                    buildResult: currentBuild.result,
                            )
                        }
                    }
                }
                stage('Maven publication') {
                    agent {
                        docker {
                            image buildImageVersion
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
                    post {
                        always {
                            ontrackValidate(
                                    project: projectName,
                                    branch: branchName,
                                    build: version,
                                    validationStamp: 'MAVEN.CENTRAL',
                                    buildResult: currentBuild.result,
                            )
                        }
                    }
                }
            }
        }

        // Release

        stage('Release') {
            agent {
                docker {
                    image buildImageVersion
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
                beforeAgent true
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
                always {
                    ontrackValidate(
                            project: projectName,
                            branch: branchName,
                            build: version,
                            validationStamp: 'GITHUB.RELEASE',
                    )
                }
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

        // Documentation

        stage('Documentation') {
            agent {
                docker {
                    image buildImageVersion
                    args "--volume /var/run/docker.sock:/var/run/docker.sock"
                }
            }
            environment {
                ONTRACK_VERSION = "${version}"
                AMS3_DELIVERY = credentials("AMS3_DELIVERY")
            }
            when {
                beforeAgent true
                branch 'release/*'
            }
            steps {
                echo "Release"

                unstash name: "delivery"
                sh '''\
                    unzip -n build/distributions/ontrack-${ONTRACK_VERSION}-delivery.zip -d ${WORKSPACE}
                    unzip -n ${WORKSPACE}/ontrack-publication.zip -d publication
                '''

                sh '''\
                    ./gradlew \\
                        --build-file publication.gradle \\
                        --info \\
                        --profile \\
                        --console plain \\
                        --stacktrace \\
                        releaseDocPrepare
                '''

                sh '''
                    s3cmd \\
                        --access_key=${AMS3_DELIVERY_USR} \\
                        --secret_key=${AMS3_DELIVERY_PSW} \\
                        --host=ams3.digitaloceanspaces.com \\
                        --host-bucket='%(bucket)s.ams3.digitaloceanspaces.com' \\
                        put \\
                        build/site/release/* \\
                        s3://ams3-delivery-space/ontrack/release/${ONTRACK_VERSION}/docs/ \\
                        --acl-public \\
                        --add-header=Cache-Control:max-age=86400 \\
                        --recursive
                '''

            }
            post {
                always {
                    ontrackValidate(
                            project: projectName,
                            branch: branchName,
                            build: version,
                            validationStamp: 'DOCUMENTATION',
                    )
                }
            }
        }

        // Merge to master (for latest release only)

        stage('Merge to master') {
            agent any
            when {
                beforeAgent true
                allOf {
                    branch "release/3.*"
                    expression {
                        ontrackGetLastBranch(project: projectName, pattern: 'release-3\\..*') == branchName
                    }
                }
            }
            steps {
                // Merge to master
                sshagent (credentials: ['SSH_JENKINS_GITHUB']) {
                    sh '''
                        git config --local user.email "jenkins@nemerosa.net"
                        git config --local user.name "Jenkins"
                        git checkout master
                        git pull origin master
                        git merge $BRANCH_NAME
                        git push origin master
                    '''
                }
            }
            post {
                always {
                    ontrackValidate(
                            project: projectName,
                            branch: branchName,
                            build: version,
                            validationStamp: 'MERGE',
                    )
                }
            }
        }

        // Master setup

        stage('Master setup') {
            agent any
            when {
                beforeAgent true
                branch 'master'
            }
            steps {
                script {
                    // Gets the latest tag
                    env.ONTRACK_VERSION = sh(
                            returnStdout: true,
                            script: 'git describe --tags --abbrev=0'
                    ).trim()
                    // Trace
                    echo "ONTRACK_VERSION=${env.ONTRACK_VERSION}"
                    // Version components
                    env.ONTRACK_VERSION_MAJOR_MINOR = extractFromVersion(env.ONTRACK_VERSION as String, /(^\d+\.\d+)\.\d.*/)
                    env.ONTRACK_VERSION_MAJOR = extractFromVersion(env.ONTRACK_VERSION as String, /(^\d+)\.\d+\.\d.*/)
                    echo "ONTRACK_VERSION_MAJOR_MINOR=${env.ONTRACK_VERSION_MAJOR_MINOR}"
                    echo "ONTRACK_VERSION_MAJOR=${env.ONTRACK_VERSION_MAJOR}"
                    // Gets the corresponding branch
                    def result = ontrackGraphQL(
                            script: '''
                                query BranchLookup($project: String!, $build: String!) {
                                  builds(project: $project, buildProjectFilter: {buildExactMatch: true, buildName: $build}) {
                                    branch {
                                      name
                                    }
                                  }
                                }
                            ''',
                            bindings: [
                                    'project': projectName,
                                    'build'  : env.ONTRACK_VERSION as String
                            ],
                    )
                    env.ONTRACK_BRANCH_NAME = result.data.builds.first().branch.name as String
                    // Trace
                    echo "ONTRACK_BRANCH_NAME=${env.ONTRACK_BRANCH_NAME}"
                }
            }
        }

        // Latest documentation

        stage('Latest documentation') {
            agent {
                docker {
                    image buildImageVersion
                }
            }
            when {
                beforeAgent true
                branch 'master'
            }
            environment {
                AMS3_DELIVERY = credentials("AMS3_DELIVERY")
            }
            steps {
                sh '''
                    s3cmd \\
                        --access_key=${AMS3_DELIVERY_USR} \\
                        --secret_key=${AMS3_DELIVERY_PSW} \\
                        --host=ams3.digitaloceanspaces.com \\
                        --host-bucket='%(bucket)s.ams3.digitaloceanspaces.com' \\
                        --recursive \\
                        --force \\
                        cp \\
                        s3://ams3-delivery-space/ontrack/release/${ONTRACK_VERSION}/docs/ \\
                        s3://ams3-delivery-space/ontrack/release/latest/docs/
                '''
            }
            post {
                always {
                    ontrackValidate(
                            project: projectName,
                            branch: env.ONTRACK_BRANCH_NAME as String,
                            build: env.ONTRACK_VERSION as String,
                            validationStamp: 'DOCUMENTATION.LATEST',
                    )
                }
            }
        }

        // Docker latest images

        stage('Docker Latest') {
            agent {
                docker {
                    image buildImageVersion
                    args "--volume /var/run/docker.sock:/var/run/docker.sock"
                }
            }
            when {
                branch "master"
            }
            environment {
                DOCKER_HUB = credentials("DOCKER_HUB")
            }
            steps {
                sh '''\
                    echo "Making sure the images are available on this node..."

                    echo ${DOCKER_REGISTRY_CREDENTIALS_PSW} | docker login docker.nemerosa.net --username ${DOCKER_REGISTRY_CREDENTIALS_USR} --password-stdin
                    docker image pull docker.nemerosa.net/nemerosa/ontrack:${ONTRACK_VERSION}

                    echo "Tagging..."

                    docker image tag docker.nemerosa.net/nemerosa/ontrack:${ONTRACK_VERSION} nemerosa/ontrack:${ONTRACK_VERSION_MAJOR_MINOR}
                    docker image tag docker.nemerosa.net/nemerosa/ontrack:${ONTRACK_VERSION} nemerosa/ontrack:${ONTRACK_VERSION_MAJOR}

                    echo "Publishing latest versions in Docker Hub..."

                    echo ${DOCKER_HUB_PSW} | docker login --username ${DOCKER_HUB_USR} --password-stdin

                    docker image push nemerosa/ontrack:${ONTRACK_VERSION_MAJOR_MINOR}
                    docker image push nemerosa/ontrack:${ONTRACK_VERSION_MAJOR}
                '''
            }
            post {
                always {
                    ontrackValidate(
                            project: projectName,
                            branch: env.ONTRACK_BRANCH_NAME as String,
                            build: env.ONTRACK_VERSION as String,
                            validationStamp: 'DOCKER.LATEST',
                    )
                }
            }
        }

        // Site generation

        stage('Site generation') {
            agent {
                docker {
                    image buildImageVersion
                }
            }
            environment {
                // GitHub OAuth token
                GRGIT_USER = credentials("JENKINS_GITHUB_TOKEN")
                GITHUB_URI = 'https://github.com/nemerosa/ontrack.git'
            }
            when {
                beforeAgent true
                branch 'master'
            }
            steps {
                echo "Getting list of releases and publishing the site..."
                sh '''\
                    ./gradlew \\
                        --info \\
                        --profile \\
                        --console plain \\
                        --stacktrace \\
                        -PontrackVersion=${ONTRACK_VERSION} \\
                        -PontrackGitHubUri=${GITHUB_URI} \\
                        site
                '''
            }
            post {
                always {
                    ontrackValidate(
                            project: projectName,
                            branch: env.ONTRACK_BRANCH_NAME as String,
                            build: env.ONTRACK_VERSION as String,
                            validationStamp: 'SITE',
                    )
                }
            }
        }

        // Production

        stage('Production') {
            agent {
                docker {
                    image buildImageVersion
                }
            }
            when {
                beforeAgent true
                branch "master"
            }
            environment {
                ONTRACK_POSTGRES = credentials('ONTRACK_POSTGRES')
            }
            steps {
                echo "Deploying ${ONTRACK_VERSION} from branch ${ONTRACK_BRANCH_NAME} in production"
                // Running the deployment
                timeout(time: 15, unit: 'MINUTES') {
                    script {
                        sshagent(credentials: ['ONTRACK_SSH_KEY']) {
                            sh '''\
#!/bin/bash

set -e

SSH_OPTIONS=StrictHostKeyChecking=no

SSH_HOST=${ONTRACK_IP}

scp -o ${SSH_OPTIONS} compose/docker-compose-prod.yml root@${SSH_HOST}:/root
ssh -o ${SSH_OPTIONS} root@${SSH_HOST} "ONTRACK_VERSION=${ONTRACK_VERSION}" "ONTRACK_POSTGRES_USER=${ONTRACK_POSTGRES_USR}" "ONTRACK_POSTGRES_PASSWORD=${ONTRACK_POSTGRES_PSW}" docker-compose --project-name prod --file /root/docker-compose-prod.yml up -d

'''
                        }
                    }
                }
            }
        }

        // Production tests

        stage('Production tests') {
            agent {
                docker {
                    image buildImageVersion
                    args "--volume /var/run/docker.sock:/var/run/docker.sock"
                }
            }
            when {
                beforeAgent true
                branch "master"
            }
            environment {
                ONTRACK_ACCEPTANCE_ADMIN = credentials("ONTRACK_ACCEPTANCE_ADMIN")
            }
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    sh '''\
#!/bin/bash
set -e

echo ${DOCKER_REGISTRY_CREDENTIALS_PSW} | docker login docker.nemerosa.net --username ${DOCKER_REGISTRY_CREDENTIALS_USR} --password-stdin

echo "(*) Launching the tests..."
docker-compose \\
    --file ontrack-acceptance/src/main/compose/docker-compose-prod-client.yml \\
    --project-name production \\
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
cp -r ontrack-acceptance/src/main/compose/build build/production

echo "(*) Removing the test environment..."
docker-compose \\
    --file ontrack-acceptance/src/main/compose/docker-compose-prod-client.yml \\
    --project-name production \\
    down

'''
                    archiveArtifacts 'build/production/**'
                    script {
                        def results = junit 'build/production/*.xml'
                        ontrackValidate(
                                project: projectName,
                                branch: env.ONTRACK_BRANCH_NAME as String,
                                build: env.ONTRACK_VERSION as String,
                                validationStamp: 'ONTRACK.SMOKE',
                                testResults: results,
                        )
                    }
                }
                success {
                    ontrackPromote(
                            project: projectName,
                            branch: env.ONTRACK_BRANCH_NAME as String,
                            build: env.ONTRACK_VERSION as String,
                            promotionLevel: 'ONTRACK',
                    )
                }
            }
        }

    }

}

@SuppressWarnings("GrMethodMayBeStatic")
@NonCPS
String extractFromVersion(String version, String pattern) {
    def matcher = (version =~ pattern)
    if (matcher.matches()) {
        return matcher.group(1)
    } else {
        throw new IllegalAccessException("Version $version does not match pattern: $pattern")
    }
}
