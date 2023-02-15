@Library("ontrack-jenkins-cli-pipeline@1.0.1") _

pipeline {

    environment {
        ONTRACK = credentials("ontrack-service-account")
    }

    parameters {
        booleanParam(
                name: 'SKIP_ACCEPTANCE',
                defaultValue: false,
                description: 'Skipping acceptance tests'
        )
    }

    agent {
        docker {
            label "do && c-8"
            image "nemerosa/ontrack-build:3.2.1"
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
                scmSkip(deleteBuild: false)
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
                // Bitbucket Cloud system tests
                ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_WORKSPACE = credentials('ontrack-test-extension-bitbucket-cloud-workspace')
                ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_USER = credentials('ontrack-test-extension-bitbucket-cloud-user')
                ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_TOKEN = credentials('ontrack-test-extension-bitbucket-cloud-token')
                ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_EXPECTED_PROJECT = 'ONTRACK'
                ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_EXPECTED_REPOSITORY = 'ontrack-pipeline-bitbucket-cloud'
                // GitHub system tests
                ONTRACK_TEST_EXTENSION_GITHUB_USER = credentials('ontrack-test-extension-github-user')
                ONTRACK_TEST_EXTENSION_GITHUB_TOKEN = credentials('ontrack-test-extension-github-token')
                ONTRACK_TEST_EXTENSION_GITHUB_ORGANIZATION = 'nemerosa'
                ONTRACK_TEST_EXTENSION_GITHUB_REPOSITORY = 'ontrack-github-integration-test'
                ONTRACK_TEST_EXTENSION_GITHUB_BRANCH = 'v1'
                ONTRACK_TEST_EXTENSION_GITHUB_ISSUE = '1'
                ONTRACK_TEST_EXTENSION_GITHUB_PR = '3'
                ONTRACK_TEST_EXTENSION_GITHUB_TEAM = 'ontrack-integration-tests'
                ONTRACK_TEST_EXTENSION_GITHUB_APP_ID = '143291'
                ONTRACK_TEST_EXTENSION_GITHUB_APP_PEM = credentials('ontrack-test-extension-github-app-pem')
                ONTRACK_TEST_EXTENSION_GITHUB_APP_INSTALLATION = 'nemerosa'
                ONTRACK_TEST_EXTENSION_GITHUB_PATHS_IMAGES_PROMOTION = 'images/iron.png'
                ONTRACK_TEST_EXTENSION_GITHUB_PATHS_IMAGES_VALIDATION = 'images/site.png'
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
                        osPackages \\
                        dockerBuild \\
                        javadocPackage \\
                        -Pdocumentation \\
                        -PbowerOptions='--allow-root' \\
                        -Dorg.gradle.jvmargs=-Xmx6144m \\
                        --stacktrace \\
                        --parallel \\
                        --no-daemon \\
                        --console plain
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

        stage('KDSL acceptance tests') {
            when {
                not {
                    branch 'master'
                }
                expression {
                    return !params.SKIP_ACCEPTANCE
                }
            }
            environment {
                ONTRACK_ACCEPTANCE_GITHUB_ORGANIZATION = credentials("ontrack-acceptance-github-organization")
                ONTRACK_ACCEPTANCE_GITHUB_TOKEN = credentials("ontrack-acceptance-github-token")
                ONTRACK_ACCEPTANCE_GITHUB_AUTOMERGETOKEN = credentials("ontrack-acceptance-github-automerge-token")
                ONTRACK_ACCEPTANCE_GITHUB_AUTOVERSIONING_POSTPROCESSING_PROCESSOR_ORG = credentials("ontrack-acceptance-github-autoversioning-postprocessing-processor-org")
                ONTRACK_ACCEPTANCE_GITHUB_AUTOVERSIONING_POSTPROCESSING_PROCESSOR_REPOSITORY = credentials("ontrack-acceptance-github-autoversioning-postprocessing-processor-repository")
                ONTRACK_ACCEPTANCE_GITHUB_AUTOVERSIONING_POSTPROCESSING_SAMPLE_ORG = credentials("ontrack-acceptance-github-autoversioning-postprocessing-sample-org")
                ONTRACK_ACCEPTANCE_GITHUB_AUTOVERSIONING_POSTPROCESSING_SAMPLE_REPOSITORY = credentials("ontrack-acceptance-github-autoversioning-postprocessing-sample-repository")
                ONTRACK_ACCEPTANCE_GITHUB_AUTOVERSIONING_POSTPROCESSING_SAMPLE_VERSION = credentials("ontrack-acceptance-github-autoversioning-postprocessing-sample-version")
            }
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    sh '''
                        ./gradlew \\
                            -Dorg.gradle.jvmargs=-Xmx2048m \\
                            --stacktrace \\
                            --console plain \\
                            :ontrack-kdsl-acceptance:kdslAcceptanceTest
                        '''
                }
            }
            post {
                always {
                    ontrackCliValidateTests(
                            stamp: 'KDSL.ACCEPTANCE',
                            pattern: 'ontrack-kdsl-acceptance/build/test-results/**/*.xml',
                    )
                }
                failure {
                    archiveArtifacts(artifacts: "ontrack-kdsl-acceptance/build/logs/**", allowEmptyArchive: true)
                }
            }
        }

        stage('Local acceptance tests') {
            when {
                not {
                    branch 'master'
                }
                expression {
                    return !params.SKIP_ACCEPTANCE
                }
            }
            steps {
                timeout(time: 25, unit: 'MINUTES') {
                    sh '''
                        cd ontrack-acceptance/src/main/compose
                        docker-compose \\
                            --project-name local \\
                            --file docker-compose.yml \\
                            up \\
                            --exit-code-from ontrack_acceptance
                        '''
                }
            }
            post {
                always {
                    sh '''
                        cd ontrack-acceptance/src/main/compose
                        docker-compose  \\
                            --project-name local \\
                            --file docker-compose.yml \\
                            logs ontrack > docker-compose-acceptance-ontrack.log
                    '''
                    sh '''
                        cd ontrack-acceptance/src/main/compose
                        docker-compose  \\
                            --project-name local \\
                            --file docker-compose.yml \\
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
                expression {
                    return !params.SKIP_ACCEPTANCE
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
                            up \\
                            --exit-code-from ontrack_acceptance
                    '''
                }
            }
            post {
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
                            down --volumes
                    '''
                }
            }
        }

        // We stop here for pull requests and feature branches

        // OS tests

//        stage('Platform tests') {
//            when {
//                anyOf {
//                    branch 'release/*'
//                }
//            }
//            stages {
//                stage('CentOS7') {
//                    steps {
//                        timeout(time: 25, unit: 'MINUTES') {
//                            sh '''
//                                echo "Preparing environment..."
//                                DOCKER_DIR=ontrack-acceptance/src/main/compose/os/centos/7/docker
//                                rm -f ${DOCKER_DIR}/*.rpm
//                                cp build/distributions/*rpm ${DOCKER_DIR}/ontrack.rpm
//
//                                echo "Launching test environment..."
//                                cd ontrack-acceptance/src/main/compose
//                                docker-compose --project-name centos --file docker-compose-centos-7.yml up --build -d ontrack
//
//                                echo "Launching Ontrack in CentOS environment..."
//                                CONTAINER=`docker-compose --project-name centos --file docker-compose-centos-7.yml ps -q ontrack`
//                                echo "... for container ${CONTAINER}"
//                                docker container exec ${CONTAINER} /etc/init.d/ontrack start
//
//                                echo "Launching tests..."
//                                docker-compose --project-name centos --file docker-compose-centos-7.yml up --exit-code-from ontrack_acceptance ontrack_acceptance
//                            '''
//                        }
//                    }
//                    post {
//                        always {
//                            sh '''
//                                mkdir -p build
//                                cp -r ontrack-acceptance/src/main/compose/build build/centos
//                                '''
//                            ontrackCliValidateTests(
//                                    stamp: 'ACCEPTANCE.CENTOS.7',
//                                    pattern: 'build/centos/*.xml',
//                            )
//                        }
//                        cleanup {
//                            sh '''
//                                cd ontrack-acceptance/src/main/compose
//                                docker-compose --project-name centos --file docker-compose-centos-7.yml down --volumes
//                                '''
//                        }
//                    }
//                }
//                // Debian
//                stage('Debian') {
//                    steps {
//                        timeout(time: 25, unit: 'MINUTES') {
//                            sh '''
//                                echo "Preparing environment..."
//                                DOCKER_DIR=ontrack-acceptance/src/main/compose/os/debian/docker
//                                rm -f ${DOCKER_DIR}/*.deb
//                                cp build/distributions/*.deb ${DOCKER_DIR}/ontrack.deb
//
//                                echo "Launching test environment..."
//                                cd ontrack-acceptance/src/main/compose
//                                docker-compose --project-name debian --file docker-compose-debian.yml up --build -d ontrack
//
//                                echo "Launching Ontrack in Debian environment..."
//                                CONTAINER=`docker-compose --project-name debian --file docker-compose-debian.yml ps -q ontrack`
//                                echo "... for container ${CONTAINER}"
//                                docker container exec ${CONTAINER} /etc/init.d/ontrack start
//
//                                echo "Launching tests..."
//                                docker-compose --project-name debian --file docker-compose-debian.yml up --build --exit-code-from ontrack_acceptance ontrack_acceptance
//                                '''
//                        }
//                    }
//                    post {
//                        always {
//                            sh '''
//                                mkdir -p build/debian
//                                cp -r ontrack-acceptance/src/main/compose/build/* build/debian/
//                                '''
//                            ontrackCliValidateTests(
//                                    stamp: 'ACCEPTANCE.DEBIAN',
//                                    pattern: 'build/debian/*.xml',
//                            )
//                        }
//                        cleanup {
//                            sh '''
//                                cd ontrack-acceptance/src/main/compose
//                                docker-compose --project-name debian --file docker-compose-debian.yml down --volumes
//                            '''
//                        }
//                    }
//                }
//            }
//        }

        // Publication

        stage('Docker Hub') {
            when {
                anyOf {
                    branch 'release/*'
                    branch 'feature/*publication'
                }
            }
            environment {
                DOCKER_HUB = credentials("docker-hub")
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

        // Release

        stage('Release') {
            environment {
                GITHUB_TOKEN = credentials("github-token")
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
                        release
                '''

            }
            post {
                success {
                    script {
                        def text = readFile file: "build/slack.txt"
                        slackSend channel: "#releases", color: "good", message: text, iconEmoji: "ontrack"
                    }
                }
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
                AMS3_DELIVERY = credentials("digitalocean-spaces")
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
                AMS3_DELIVERY = credentials("digitalocean-spaces")
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
                DOCKER_HUB = credentials("docker-hub")
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
                GRGIT_USER = credentials("github-token")
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
