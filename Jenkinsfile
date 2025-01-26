@Library("ontrack-jenkins-cli-pipeline@4.9") _

pipeline {

    environment {
        ONTRACK = credentials("ontrack-service-account")
    }

    parameters {
        booleanParam(
                name: 'JUST_BUILD_AND_PUSH',
                defaultValue: false,
                description: 'Just create the Docker images and push them'
        )
        booleanParam(
                name: 'SKIP_KDSL_ACCEPTANCE',
                defaultValue: false,
                description: 'Skipping KDSL acceptance tests'
        )
        booleanParam(
                name: 'SKIP_NEXT_UI_TESTS',
                defaultValue: false,
                description: 'Skipping legacy acceptance tests'
        )
        booleanParam(
                name: 'SKIP_LEGACY_ACCEPTANCE',
                defaultValue: false,
                description: 'Skipping legacy acceptance tests'
        )
        booleanParam(
                name: 'SKIP_BITBUCKET_CLOUD_IT',
                defaultValue: false,
                description: 'Skipping integration tests for Bitbucket Cloud'
        )
    }

    agent {
        docker {
            label "do && c-16"
            image "nemerosa/ontrack-build:4.0.2"
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
                    validations: [
                        [
                            name: 'KDSL.ACCEPTANCE',
                            tests: [
                                warningIfSkipped: false,
                            ],
                        ],
                    ],
                    promotions: [
                        BRONZE: [
                            validations: [
                                'BUILD',
                                'KDSL.ACCEPTANCE',
                                'ACCEPTANCE',
                            ],
                        ],
                        RELEASE: [
                            promotions: [
                                'BRONZE',
                            ],
                            validations: [
                                'GITHUB.RELEASE',
                            ]
                        ],
                    ]
                )
                ontrackCliSetupBranchNotifications(
                        name: 'On validation error',
                        channel: 'slack',
                        channelConfig: [
                                channel: '#notifications',
                                type: 'ERROR'
                        ],
                        events: [
                                'new_validation_run',
                        ],
                        keywords: 'failed',
                        contentTemplate: '''\
                            Build ${build} has failed on ${validationStamp}. 
                        '''
                )
                ontrackCliSetupPromotionLevelNotifications(
                        name: 'On BRONZE',
                        promotion: 'BRONZE',
                        channel: 'slack',
                        channelConfig: [
                                channel: '#notifications',
                                type: 'SUCCESS'
                        ],
                        events: [
                                'new_promotion_run',
                        ],
                        contentTemplate: '''\
                            Build ${build} has been promoted to ${promotionLevel}. 
                        '''
                )
                ontrackCliSetupPromotionLevelNotifications(
                        name: 'On RELEASE',
                        promotion: 'RELEASE',
                        channel: 'slack',
                        channelConfig: [
                                channel: env.BRANCH_NAME ==~ /^release\/\d+\.\d+$/ ? '#releases' : '#internal-releases',
                                type: 'SUCCESS'
                        ],
                        events: [
                                'new_promotion_run',
                        ],
                        contentTemplate: '''\
                            Ontrack ${build} has been released.
                            
                            ${promotionRun.changelog?title=true}
                            ''',
                )
                ontrackCliSetupPromotionLevelNotifications(
                        name: 'On RELEASE deploy the Demo Beta',
                        promotion: 'RELEASE',
                        channel: 'workflow',
                        channelConfig: [
                                workflow: [
                                        name: "Deploy Demo Beta",
                                        nodes: [
                                                [
                                                        id: "start",
                                                        description: "Start deployment",
                                                        executorId: "slot-pipeline-creation",
                                                        data: [
                                                                environment: "demo-beta",
                                                        ]
                                                ]
                                        ]
                                ]
                        ],
                        events: [
                                'new_promotion_run',
                        ],
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
                ONTRACK_TEST_EXTENSION_GITHUB_PR = '3'
                ONTRACK_TEST_EXTENSION_GITHUB_TEAM = 'ontrack-integration-tests'
                ONTRACK_TEST_EXTENSION_GITHUB_APP_ID = '143291'
                ONTRACK_TEST_EXTENSION_GITHUB_APP_PEM = credentials('ontrack-test-extension-github-app-pem')
                ONTRACK_TEST_EXTENSION_GITHUB_APP_INSTALLATION = 'nemerosa'
                ONTRACK_TEST_EXTENSION_GITHUB_PATHS_IMAGES_PROMOTION = 'images/iron.png'
                ONTRACK_TEST_EXTENSION_GITHUB_PATHS_IMAGES_VALIDATION = 'images/site.png'
                ONTRACK_TEST_EXTENSION_GITHUB_CHANGELOG_FROM = '5b0db7c7132eaf29820b42aef3a018a47e1f411f'
                ONTRACK_TEST_EXTENSION_GITHUB_CHANGELOG_TO = '36e95eded56bcfead06a55238ccb315fbfe211a8'
                ONTRACK_TEST_EXTENSION_GITHUB_CHANGELOG_MESSAGES = 'nemerosa/ontrack#978 Promotion image|nemerosa/ontrack#978 Validation image|nemerosa/ontrack#928 Explicitly skipping the jobs|nemerosa/ontrack#928 Sample GitHub ingestion configuration file'
                ONTRACK_TEST_EXTENSION_GITHUB_ISSUES_FROM = '36e95eded56bcfead06a55238ccb315fbfe211a8'
                ONTRACK_TEST_EXTENSION_GITHUB_ISSUES_ISSUE = '1'
                ONTRACK_TEST_EXTENSION_GITHUB_ISSUES_ISSUESUMMARY = 'Test issue'
                ONTRACK_TEST_EXTENSION_GITHUB_ISSUES_TO = '901130bc4655a8180c2034f0619768c7343095bb'
                ONTRACK_TEST_EXTENSION_GITHUB_ISSUES_MESSAGES = '#1 Commit with issue'
                ONTRACK_TEST_EXTENSION_GITHUB_ISSUES_ISSUELABELS = 'type:defect'
                ONTRACK_TEST_EXTENSION_GITHUB_ISSUES_MILESTONE = 'v1'
            }
            steps {
                sh ''' ./gradlew clean versionDisplay versionFile --no-daemon'''
                script {
                    // Additional options
                    env.ONTRACK_TEST_EXTENSION_BITBUCKET_CLOUD_IGNORE = params.SKIP_BITBUCKET_CLOUD_IT
                    // Reads version information
                    def props = readProperties(file: 'build/version.properties')
                    env.VERSION = props.VERSION_DISPLAY
                    env.GIT_COMMIT = props.VERSION_COMMIT
                    // Creates a build
                    ontrackCliBuild(name: VERSION)
                }
                echo "Version = ${VERSION}"
                script {
                    if (params.JUST_BUILD_AND_PUSH) {
                        sh '''
                            ./gradlew \\
                                dockerBuild \\
                                -PbowerOptions='--allow-root' \\
                                -Dorg.gradle.jvmargs=-Xmx6144m \\
                                --stacktrace \\
                                --parallel \\
                                --no-daemon \\
                                --console plain
                        '''
                    } else {
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
                }
            }
            post {
                always {
                    recordIssues(tools: [kotlin(), javaDoc(), java()])
                    // Build validation stamps
                    ontrackCliValidateTests(
                        stamp: 'BUILD',
                        pattern: '**/build/test-results/**/*.xml',
                    )
                    ontrackCliValidateTests(
                            stamp: 'UI_UNIT',
                            pattern: 'ontrack-web-core/reports/*.xml',
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
                    return !params.SKIP_KDSL_ACCEPTANCE && !params.JUST_BUILD_AND_PUSH
                }
            }
            environment {
                ONTRACK_ACCEPTANCE_GITHUB_ORGANIZATION = credentials("ontrack-acceptance-github-organization")
                ONTRACK_ACCEPTANCE_GITHUB_TOKEN = credentials("ontrack-acceptance-github-token")
            }
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    sh '''
                        ./gradlew \\
                            -Dorg.gradle.jvmargs=-Xmx2048m \\
                            --stacktrace \\
                            --console plain \\
                            --parallel \\
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
                    script {
                        sh 'mv ontrack-kdsl-acceptance/build/logs ontrack-kdsl-acceptance/build/logs-kdsl'
                    }
                    archiveArtifacts(artifacts: "ontrack-kdsl-acceptance/build/logs-kdsl/**", allowEmptyArchive: true)
                }
            }
        }

        stage('Local Next UI tests') {
            when {
                not {
                    branch 'master'
                }
                expression {
                    return !params.SKIP_NEXT_UI_TESTS && !params.JUST_BUILD_AND_PUSH
                }
            }
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    sh '''
                        ./gradlew \\
                            -Dorg.gradle.jvmargs=-Xmx3072m \\
                            --stacktrace \\
                            --console plain \\
                            --parallel \\
                            :ontrack-web-tests:uiTest
                        '''
                }
            }
            post {
                always {
                    ontrackCliValidateTests(
                            stamp: 'PLAYWRIGHT',
                            pattern: 'ontrack-web-tests/reports/junit/*.xml',
                    )
                }
                failure {
                    script {
                        sh 'mv ontrack-kdsl-acceptance/build/logs ontrack-kdsl-acceptance/build/logs-ui'
                    }
                    archiveArtifacts(artifacts: "ontrack-kdsl-acceptance/build/logs-ui/**", allowEmptyArchive: true)
                    archiveArtifacts(artifacts: "ontrack-web-tests/reports/html/**", allowEmptyArchive: true)
                }
            }
        }

        stage('Local acceptance tests') {
            when {
                not {
                    branch 'master'
                }
                expression {
                    return !params.SKIP_LEGACY_ACCEPTANCE && !params.JUST_BUILD_AND_PUSH
                }
            }
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    sh '''
                        ./gradlew \\
                            -Dorg.gradle.jvmargs=-Xmx3072m \\
                            --stacktrace \\
                            --console plain \\
                            --parallel \\
                            :ontrack-acceptance:acceptanceTest
                        '''
                }
            }
            post {
                always {
                    ontrackCliValidateTests(
                            stamp: 'ACCEPTANCE',
                            pattern: 'ontrack-acceptance/build/test-results/**/*.xml',
                    )
                }
                failure {
                    archiveArtifacts(artifacts: "ontrack-acceptance/build/logs/**", allowEmptyArchive: true)
                }
            }
        }

        // We stop here for pull requests and feature branches

        // Publication

        stage('Docker Hub') {
            when {
                anyOf {
                    branch 'release/*'
                    branch 'feature/*publication'
                    expression {
                        params.JUST_BUILD_AND_PUSH
                    }
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
                    docker image push nemerosa/ontrack-ui:${VERSION}
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
                        -PontrackToken=${ONTRACK_PSW} \\
                        -PgitHubToken=${GITHUB_TOKEN} \\
                        -PgitHubCommit=${GIT_COMMIT} \\
                        -PgitHubChangeLogReleaseBranch=${ONTRACK_BRANCH_NAME} \\
                        -PgitHubChangeLogCurrentBuild=${ONTRACK_BUILD_NAME} \\
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
                        -PontrackToken=${ONTRACK_PSW} \\
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
