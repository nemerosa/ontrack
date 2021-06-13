@Library("ontrack-jenkins-cli-pipeline@1.0.1") _

pipeline {

    environment {
        ONTRACK = credentials("ONTRACK_SERVICE_ACCOUNT")
        DOCKER_REGISTRY_CREDENTIALS = credentials("DOCKER_NEMEROSA")
        CODECOV_TOKEN = credentials("CODECOV_TOKEN")
        GPG_KEY = credentials("GPG_KEY")
        GPG_KEY_RING = credentials("GPG_KEY_RING")
    }

    agent {
        docker {
            image "nemerosa/ontrack-build:3.1.0"
            args "--volume /var/run/docker.sock:/var/run/docker.sock --network host"
        }
    }

    options {
        // General Jenkins job properties
        buildDiscarder(logRotator(numToKeepStr: '40'))
        // Timestamps
        timestamps()
        // No durability
        durabilityHint('PERFORMANCE_OPTIMIZED')
        // ANSI colours
        ansiColor('xterm')
        // No concurrent builds
        disableConcurrentBuilds()
    }

    stages {

        stage('Setup') {
            when {
                not {
                    branch 'master'
                }
            }
            steps {
                ontrackCliSetup(
                    autoValidationStamps: true,
                    promotions: [
                        RELEASE: [
                            validations: [
                                'GITHUB.RELEASE',
                            ]
                        ]
                    ]
                )
            }
        }

        stage('Build') {
            when {
                not {
                    branch 'master'
                }
            }
            environment {
                ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_WORKSPACE = credentials('ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_WORKSPACE')
                ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_USER = credentials('ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_USER')
                ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_TOKEN = credentials('ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_TOKEN')
                ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_EXPECTED_PROJECT = 'ONTRACK'
                ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_EXPECTED_REPOSITORY = 'ontrack-pipeline-bitbucket-cloud/'
            }
            steps {
                sh ''' ./gradlew clean versionDisplay versionFile --no-daemon'''
                script {
                    // Reads version information
                    def props = readProperties(file: 'build/version.properties')
                    env.VERSION = props.VERSION_DISPLAY
                    env.GIT_COMMIT = props.VERSION_COMMIT
                    // Creates a build
                    ontrackCliBuild(name: VERSION)
                }
                echo "Version = ${VERSION}"
                sh '''
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
                        -Psigning.keyId=${GPG_KEY_USR} \\
                        -Psigning.password=${GPG_KEY_PSW} \\
                        -Psigning.secretKeyRingFile=${GPG_KEY_RING} \\
                        -Dorg.gradle.jvmargs=-Xmx6144m \\
                        --stacktrace \\
                        --parallel \\
                        --no-daemon \\
                        --console plain
                '''
                // Jacoco report available at build/reports/jacoco/build.xml
                sh '''
                    echo "(*) Building the test extension..."
                    cd ontrack-extension-test
                    ./gradlew \\
                        clean \\
                        build \\
                        -PontrackVersion=${VERSION} \\
                        -PbowerOptions='--allow-root' \\
                        -Dorg.gradle.jvmargs=-Xmx2048m \\
                        --stacktrace \\
                        --console plain
                '''
                echo "Pushing image to registry..."
                sh '''
                    docker tag nemerosa/ontrack:${VERSION} docker.nemerosa.net/nemerosa/ontrack:${VERSION}
                    docker tag nemerosa/ontrack-acceptance:${VERSION} docker.nemerosa.net/nemerosa/ontrack-acceptance:${VERSION}
                    docker tag nemerosa/ontrack-extension-test:${VERSION} docker.nemerosa.net/nemerosa/ontrack-extension-test:${VERSION}
                '''
            }
            post {
                always {
                    recordIssues(tools: [kotlin(), javaDoc(), java()])
                    // Build validation stamp
                    ontrackCliValidateTests(
                        stamp: 'BUILD',
                        pattern: '**/build/test-results/**/*.xml',
                    )
                }
            }
        }

        stage('Local acceptance tests') {
            when {
                not {
                    branch 'master'
                }
            }
            steps {
                timeout(time: 25, unit: 'MINUTES') {
                    sh '''
                        cd ontrack-acceptance/src/main/compose
                        docker-compose \\
                            --project-name local \\
                            --file docker-compose.yml \\
                            --file docker-compose-jacoco.yml \\
                            up \\
                            --exit-code-from ontrack_acceptance
                        '''
                }
            }
            post {
                success {
                    sh '''
                        echo "Getting Jacoco coverage"
                        mkdir -p build/jacoco/
                        cp ontrack-acceptance/src/main/compose/jacoco/jacoco.exec build/jacoco/acceptance.exec
                        cp ontrack-acceptance/src/main/compose/jacoco-dsl/jacoco.exec build/jacoco/dsl.exec
                    '''
                    // Collection of coverage in Docker
                    sh '''
                        ./gradlew \\
                            codeDockerCoverageReport \\
                            -x processResources \\
                            -PjacocoExecFile=build/jacoco/acceptance.exec \\
                            -PjacocoReportFile=build/reports/jacoco/acceptance.xml \\
                            --parallel \\
                            --stacktrace \\
                            --console plain
                    '''
                    // Collection of coverage in DSL
                    sh '''
                        ./gradlew \\
                            codeDockerCoverageReport \\
                            -x processResources \\
                            -PjacocoExecFile=build/jacoco/dsl.exec \\
                            -PjacocoReportFile=build/reports/jacoco/dsl.xml \\
                            --parallel \\
                            --stacktrace \\
                            --console plain
                    '''
                }
                always {
                    sh '''
                        cd ontrack-acceptance/src/main/compose
                        docker-compose  \\
                            --project-name local \\
                            --file docker-compose.yml \\
                            --file docker-compose-jacoco.yml \\
                            logs ontrack > docker-compose-acceptance-ontrack.log
                    '''
                    sh '''
                        cd ontrack-acceptance/src/main/compose
                        docker-compose  \\
                            --project-name local \\
                            --file docker-compose.yml \\
                            --file docker-compose-jacoco.yml \\
                            logs selenium > docker-compose-acceptance-selenium.log
                    '''
                    archiveArtifacts(artifacts: "ontrack-acceptance/src/main/compose/docker-compose-acceptance-ontrack.log", allowEmptyArchive: true)
                    archiveArtifacts(artifacts: "ontrack-acceptance/src/main/compose/docker-compose-acceptance-selenium.log", allowEmptyArchive: true)
                    archiveArtifacts(artifacts: "ontrack-acceptance/src/main/compose/build/**", allowEmptyArchive: true)
                    sh '''
                        rm -rf build/acceptance
                        mkdir -p build
                        cp -r ontrack-acceptance/src/main/compose/build build/acceptance
                        '''
                    ontrackCliValidateTests(
                        stamp: 'ACCEPTANCE',
                        pattern: 'build/acceptance/*.xml',
                    )
                }
                cleanup {
                    sh '''
                        cd ontrack-acceptance/src/main/compose
                        docker-compose \\
                            --project-name local \\
                            --file docker-compose.yml \\
                            --file docker-compose-jacoco.yml \\
                            down --volumes
                    '''
                }
            }
        }

        stage('Local extension tests') {
            when {
                not {
                    branch "master"
                }
            }
            steps {
                timeout(time: 25, unit: 'MINUTES') {
                    // Cleanup
                    sh ' rm -rf ontrack-acceptance/src/main/compose/build '
                    // Launches the tests
                    sh '''
                        cd ontrack-acceptance/src/main/compose
                        docker-compose \\
                            --project-name ext \\
                            --file docker-compose-ext.yml \\
                            --file docker-compose-jacoco.yml up \\
                            --exit-code-from ontrack_acceptance
                    '''
                }
            }
            post {
                success {
                    sh '''
                        echo "Getting Jacoco coverage"
                        mkdir -p build/jacoco/
                        cp ontrack-acceptance/src/main/compose/jacoco/jacoco.exec build/jacoco/extension.exec
                    '''
                    // Collection of coverage in Docker
                    sh '''
                        ./gradlew \\
                            codeDockerCoverageReport \\
                            -x processResources \\
                            -PjacocoExecFile=build/jacoco/extension.exec \\
                            -PjacocoReportFile=build/reports/jacoco/extension.xml \\
                            --parallel \\
                            --stacktrace \\
                            --console plain
                    '''
                }
                always {
                    sh '''
                        mkdir -p build
                        rm -rf build/extension
                        cp -r ontrack-acceptance/src/main/compose/build build/extension
                    '''
                    ontrackCliValidateTests(
                            stamp: 'EXTENSIONS',
                            pattern: 'build/extension/*.xml',
                    )
                }
                cleanup {
                    sh '''
                        cd ontrack-acceptance/src/main/compose
                        docker-compose \\
                            --project-name ext \\
                            --file docker-compose-ext.yml \\
                            --file docker-compose-jacoco.yml \\
                            down --volumes
                    '''
                }
            }
        }

        stage('Local Vault tests') {
            when {
                not {
                    branch "master"
                }
            }
            steps {
                timeout(time: 25, unit: 'MINUTES') {
                    // Cleanup
                    sh ' rm -rf ontrack-acceptance/src/main/compose/build '
                    // Launches the tests
                    sh '''
                        cd ontrack-acceptance/src/main/compose
                        docker-compose \\
                            --project-name vault \\
                            --file docker-compose-vault.yml \\
                            --file docker-compose-jacoco.yml up \\
                            --exit-code-from ontrack_acceptance
                    '''
                }
            }
            post {
                success {
                    sh '''
                        echo "Getting Jacoco coverage"
                        mkdir -p build/jacoco/
                        cp ontrack-acceptance/src/main/compose/jacoco/jacoco.exec build/jacoco/vault.exec
                    '''
                    // Collection of coverage in Docker
                    sh '''
                        ./gradlew \\
                            codeDockerCoverageReport \\
                            -x processResources \\
                            -PjacocoExecFile=build/jacoco/vault.exec \\
                            -PjacocoReportFile=build/reports/jacoco/vault.xml \\
                            --parallel \\
                            --stacktrace \\
                            --console plain
                    '''
                }
                always {
                    sh '''
                        mkdir -p build
                        rm -rf build/vault
                        cp -r ontrack-acceptance/src/main/compose/build build/vault
                    '''
                    ontrackCliValidateTests(
                            stamp: 'VAULT',
                            pattern: 'build/vault/*.xml',
                    )
                }
                cleanup {
                    sh '''
                        cd ontrack-acceptance/src/main/compose
                        docker-compose \\
                            --project-name vault \\
                            --file docker-compose-vault.yml \\
                            --file docker-compose-jacoco.yml \\
                            down --volumes
                    '''
                }
            }
        }

        stage('Codecov upload') {
            when {
                not {
                    branch "master"
                }
            }
            steps {
                // Upload to Codecov
                sh '''
                    curl -s https://codecov.io/bash -o codecov.sh
                    cat codecov.sh | bash -s -- -c -F build -f build/reports/jacoco/build.xml
                    cat codecov.sh | bash -s -- -c -F acceptance -f build/reports/jacoco/acceptance.xml
                    cat codecov.sh | bash -s -- -c -F dsl -f build/reports/jacoco/dsl.xml
                    cat codecov.sh | bash -s -- -c -F extension -f build/reports/jacoco/extension.xml
                    cat codecov.sh | bash -s -- -c -F vault -f build/reports/jacoco/vault.xml
                    '''
            }
        }

        // We stop here for pull requests and feature branches

        // OS tests

        stage('Platform tests') {
            when {
                anyOf {
                    branch 'release/*'
                }
            }
            stages {
                stage('CentOS7') {
                    steps {
                        timeout(time: 25, unit: 'MINUTES') {
                            sh '''
                                echo "Preparing environment..."
                                DOCKER_DIR=ontrack-acceptance/src/main/compose/os/centos/7/docker
                                rm -f ${DOCKER_DIR}/*.rpm
                                cp build/distributions/*rpm ${DOCKER_DIR}/ontrack.rpm

                                echo "Launching test environment..."
                                cd ontrack-acceptance/src/main/compose
                                docker-compose --project-name centos --file docker-compose-centos-7.yml up --build -d ontrack

                                echo "Launching Ontrack in CentOS environment..."
                                CONTAINER=`docker-compose --project-name centos --file docker-compose-centos-7.yml ps -q ontrack`
                                echo "... for container ${CONTAINER}"
                                docker container exec ${CONTAINER} /etc/init.d/ontrack start

                                echo "Launching tests..."
                                docker-compose --project-name centos --file docker-compose-centos-7.yml up --exit-code-from ontrack_acceptance ontrack_acceptance
                            '''
                        }
                    }
                    post {
                        always {
                            sh '''
                                mkdir -p build
                                cp -r ontrack-acceptance/src/main/compose/build build/centos
                                '''
                            ontrackCliValidateTests(
                                    stamp: 'ACCEPTANCE.CENTOS.7',
                                    pattern: 'build/centos/*.xml',
                            )
                        }
                        cleanup {
                            sh '''
                                cd ontrack-acceptance/src/main/compose
                                docker-compose --project-name centos --file docker-compose-centos-7.yml down --volumes
                                '''
                        }
                    }
                }
                // Debian
                stage('Debian') {
                    steps {
                        timeout(time: 25, unit: 'MINUTES') {
                            sh '''
                                echo "Preparing environment..."
                                DOCKER_DIR=ontrack-acceptance/src/main/compose/os/debian/docker
                                rm -f ${DOCKER_DIR}/*.deb
                                cp build/distributions/*.deb ${DOCKER_DIR}/ontrack.deb

                                echo "Launching test environment..."
                                cd ontrack-acceptance/src/main/compose
                                docker-compose --project-name debian --file docker-compose-debian.yml up --build -d ontrack

                                echo "Launching Ontrack in Debian environment..."
                                CONTAINER=`docker-compose --project-name debian --file docker-compose-debian.yml ps -q ontrack`
                                echo "... for container ${CONTAINER}"
                                docker container exec ${CONTAINER} /etc/init.d/ontrack start

                                echo "Launching tests..."
                                docker-compose --project-name debian --file docker-compose-debian.yml up --build --exit-code-from ontrack_acceptance ontrack_acceptance
                                '''
                        }
                    }
                    post {
                        always {
                            sh '''
                                mkdir -p build/debian
                                cp -r ontrack-acceptance/src/main/compose/build/* build/debian/
                                '''
                            ontrackCliValidateTests(
                                    stamp: 'ACCEPTANCE.DEBIAN',
                                    pattern: 'build/debian/*.xml',
                            )
                        }
                        cleanup {
                            sh '''
                                cd ontrack-acceptance/src/main/compose
                                docker-compose --project-name debian --file docker-compose-debian.yml down --volumes
                            '''
                        }
                    }
                }
            }
        }

        // Publication

        stage('Publication') {
            when {
                anyOf {
                    branch 'release/*'
                    branch 'feature/*publication'
                }
            }
            stages {
                stage('Docker Hub') {
                    environment {
                        DOCKER_HUB = credentials("DOCKER_HUB")
                    }
                    steps {
                        echo "Docker push"
                        sh '''
                            echo ${DOCKER_HUB_PSW} | docker login --username ${DOCKER_HUB_USR} --password-stdin
                            docker image push nemerosa/ontrack:${VERSION}
                        '''
                    }
                    post {
                        always {
                            ontrackCliValidate(
                                    stamp: 'DOCKER.HUB'
                            )
                        }
                    }
                }
                stage('Maven Central') {
                    environment {
                        OSSRH = credentials("OSSRH")
                    }
                    steps {
                        sh '''
                            git status
                            ./gradlew \\
                                publishToMavenCentral \\
                                -Pdocumentation \\
                                -PbowerOptions='--allow-root' \\
                                -Psigning.keyId=${GPG_KEY_USR} \\
                                -Psigning.password=${GPG_KEY_PSW} \\
                                -Psigning.secretKeyRingFile=${GPG_KEY_RING} \\
                                -PossrhUsername=${OSSRH_USR} \\
                                -PossrhPassword=${OSSRH_PSW} \\
                                --info \\
                                --console plain \\
                                --stacktrace
                        '''
                    }
                    post {
                        always {
                            ontrackCliValidate(
                                    stamp: 'MAVEN.CENTRAL'
                            )
                        }
                    }
                }
            }
        }

        // Release

        stage('Release') {
            environment {
                GITHUB_TOKEN = credentials("JENKINS_GITHUB_TOKEN")
                GITTER_TOKEN = credentials("GITTER_TOKEN")
            }
            when {
                beforeAgent true
                anyOf {
                    branch 'release/*'
                }
            }
            steps {
                sh '''
                    ./gradlew \\
                        --info \\
                        --console plain \\
                        --stacktrace \\
                        -PontrackUser=${ONTRACK_USR} \\
                        -PontrackPassword=${ONTRACK_PSW} \\
                        -PgitHubToken=${GITHUB_TOKEN} \\
                        -PgitHubCommit=${GIT_COMMIT} \\
                        -PgitHubChangeLogReleaseBranch=${ONTRACK_BRANCH_NAME} \\
                        -PgitterToken=${GITTER_TOKEN} \\
                        release
                '''

            }
            post {
                always {
                    ontrackCliValidate(
                            stamp: 'GITHUB.RELEASE',
                    )
                }
            }
        }

        // Documentation

        stage('Documentation') {
            environment {
                AMS3_DELIVERY = credentials("AMS3_DELIVERY")
            }
            when {
                beforeAgent true
                allOf {
                    not {
                        anyOf {
                            branch '*alpha'
                            branch '*beta'
                        }
                    }
                    anyOf {
                        branch 'release/*'
                        branch 'develop'
                    }
                }
            }
            steps {
                script {
                    if (BRANCH_NAME == 'develop') {
                        env.DOC_DIR = 'develop'
                    } else {
                        env.DOC_DIR = env.VERSION
                    }
                }

                sh '''
                    s3cmd \\
                        --access_key=${AMS3_DELIVERY_USR} \\
                        --secret_key=${AMS3_DELIVERY_PSW} \\
                        --host=ams3.digitaloceanspaces.com \\
                        --host-bucket='%(bucket)s.ams3.digitaloceanspaces.com' \\
                        put \\
                        build/site/release/* \\
                        s3://ams3-delivery-space/ontrack/release/${DOC_DIR}/docs/ \\
                        --acl-public \\
                        --add-header=Cache-Control:max-age=86400 \\
                        --recursive
                '''

            }
            post {
                always {
                    ontrackCliValidate(
                            stamp: 'DOCUMENTATION',
                    )
                }
            }
        }

        // Merge to master (for latest release only)

        stage('Merge to master') {
            when {
                allOf {
                    branch "release/4.*"
                    expression {
                        ontrackCliLastBranch(pattern: 'release-4\\..*') == ONTRACK_BRANCH_NAME
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
                    ontrackCliValidate(stamp: 'MERGE')
                }
            }
        }

        // Master setup

        stage('Master setup') {
            when {
                branch 'master'
            }
            steps {
                ontrackCliSetup(setup: false)
                script {
                    // Gets the latest tag
                    env.ONTRACK_VERSION = sh(
                            returnStdout: true,
                            script: 'git describe --tags --abbrev=0'
                    ).trim()
                    // Trace
                    echo "ONTRACK_VERSION=${env.ONTRACK_VERSION}"
                    // Version components
                    env.ONTRACK_VERSION_MAJOR_MINOR = extractFromVersion(env.ONTRACK_VERSION as String, /(^\d+\.\d+)(?:-beta)?\.\d.*/)
                    env.ONTRACK_VERSION_MAJOR = extractFromVersion(env.ONTRACK_VERSION as String, /(^\d+)\.\d+(?:-beta)?\.\d.*/)
                    echo "ONTRACK_VERSION_MAJOR_MINOR=${env.ONTRACK_VERSION_MAJOR_MINOR}"
                    echo "ONTRACK_VERSION_MAJOR=${env.ONTRACK_VERSION_MAJOR}"
                    // Gets the corresponding branch
                    def result = ontrackCliGraphQL(
                            query: '''
                                query BranchLookup($project: String!, $build: String!) {
                                  builds(project: $project, buildProjectFilter: {buildExactMatch: true, buildName: $build}) {
                                    branch {
                                      name
                                    }
                                  }
                                }
                            ''',
                            variables: [
                                project: env.ONTRACK_PROJECT_NAME as String,
                                build  : env.ONTRACK_VERSION as String,
                            ],
                    )
                    env.ONTRACK_TARGET_BRANCH_NAME = result.data.builds.first().branch.name as String
                    // Trace
                    echo "ONTRACK_TARGET_BRANCH_NAME=${env.ONTRACK_TARGET_BRANCH_NAME}"
                }
            }
        }

        // Latest documentation

        stage('Latest documentation') {
            when {
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
                    ontrackCliValidate(
                            branch: env.ONTRACK_TARGET_BRANCH_NAME as String,
                            build: env.ONTRACK_VERSION as String,
                            stamp: 'DOCUMENTATION.LATEST',
                    )
                }
            }
        }

        // Docker latest images

        stage('Docker Latest') {
            when {
                branch "master"
            }
            environment {
                DOCKER_HUB = credentials("DOCKER_HUB")
            }
            steps {
                sh '''\
                    echo "Making sure the images are available on this node..."

                    docker image pull nemerosa/ontrack:${ONTRACK_VERSION}

                    echo "Tagging..."

                    docker image tag nemerosa/ontrack:${ONTRACK_VERSION} nemerosa/ontrack:${ONTRACK_VERSION_MAJOR_MINOR}
                    docker image tag nemerosa/ontrack:${ONTRACK_VERSION} nemerosa/ontrack:${ONTRACK_VERSION_MAJOR}

                    echo "Publishing latest versions in Docker Hub..."

                    echo ${DOCKER_HUB_PSW} | docker login --username ${DOCKER_HUB_USR} --password-stdin

                    docker image push nemerosa/ontrack:${ONTRACK_VERSION_MAJOR_MINOR}
                    docker image push nemerosa/ontrack:${ONTRACK_VERSION_MAJOR}
                '''
            }
            post {
                always {
                    ontrackCliValidate(
                            branch: env.ONTRACK_TARGET_BRANCH_NAME as String,
                            build: env.ONTRACK_VERSION as String,
                            stamp: 'DOCKER.LATEST',
                    )
                }
            }
        }

        // Site generation

        stage('Site generation') {
            environment {
                // GitHub OAuth token
                GRGIT_USER = credentials("JENKINS_GITHUB_TOKEN")
                GITHUB_URI = 'https://github.com/nemerosa/ontrack.git'
            }
            when {
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
                        -PontrackUser=${ONTRACK_USR} \\
                        -PontrackPassword=${ONTRACK_PSW} \\
                        -PontrackVersion=${ONTRACK_VERSION} \\
                        -PontrackGitHubUri=${GITHUB_URI} \\
                        site
                '''
            }
            post {
                always {
                    ontrackCliValidate(
                            branch: env.ONTRACK_TARGET_BRANCH_NAME as String,
                            build: env.ONTRACK_VERSION as String,
                            stamp: 'SITE',
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
        error("Version $version does not match pattern: $pattern")
    }
}
