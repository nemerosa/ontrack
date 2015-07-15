/**
 * List of global parameters:
 *
 * - JDK8u25
 *
 * List of global passwords:
 *
 * - DOCKER_PASSWORD
 * - GITHUB_TOKEN
 *
 * List of plug-ins:
 *
 * - Delivery pipeline
 * - Build pipeline
 * - Parameterized trigger
 * - Git
 * - Folders
 * - Set build name
 * - Xvfb
 * - Ontrack
 *
 * The Seed plug-in will give the following parameters to this scripts, available directly as variables:
 *
 * - raw parameters (seed generator input + scm branch)
 *   - PROJECT - raw project name, like nemerosa/seed in GitHub
 *   - PROJECT_CLASS
 *   - PROJECT_SCM_TYPE
 *   - PROJECT_SCM_URL
 *   - BRANCH - basic branch name in the SCM, like branches/xxx in SVN
 *
 * - computed parameters:
 *   - SEED_PROJECT: project normalised name
 *   - SEED_BRANCH: branch normalised name
 *
 * The jobs are generated directly at the level of the branch seed job, so no folder needs to be created for the
 * branch itself.
 */

/**
 * Branch
 */

// Branch type
def branchType
int pos = BRANCH.indexOf('/')
if (pos > 0) {
    branchType = BRANCH.substring(0, pos)
} else {
    branchType = BRANCH
}
println "BRANCH = ${BRANCH}"
println "\tBranchType = ${branchType}"

// Extracting the delivery
def extractDeliveryArtifacts(Object dsl) {
    dsl.steps {
        // Cleaning the workspace
        shell 'rm -rf *'
        // Copy of artifacts
        copyArtifacts("${SEED_PROJECT}-${SEED_BRANCH}-build") {
            flatten()
            buildSelector {
                upstreamBuild()
            }
        }
        // Expanding the delivery ZIP
        shell 'unzip ontrack-*-delivery.zip'
    }
}

// Keeps only some version types
if (['master', 'feature', 'release', 'hotfix'].contains(branchType)) {

    // Normalised branch name
    def NAME = BRANCH.replaceAll(/[^A-Za-z0-9\.\-_]/, '-')
    println "\tGenerating ${NAME}..."

    // Build job
    freeStyleJob("${PROJECT}-${NAME}-build") {
        logRotator(-1, 40)
        deliveryPipelineConfiguration('Commit', 'Build')
        jdk 'JDK8u25'
        scm {
            git {
                remote {
                    url "git@github.com:nemerosa/ontrack.git"
                    branch "origin/${BRANCH}"
                }
                wipeOutWorkspace()
                localBranch "${BRANCH}"
            }
        }
        steps {
            gradle 'clean versionDisplay versionFile test integrationTest build  --info --profile --parallel'
            environmentVariables {
                propertiesFile 'build/version.properties'
            }
        }
        publishers {
            archiveJunit("**/build/test-results/*.xml")
            archiveArtifacts {
                pattern 'ontrack-ui/build/libs/ontrack-ui-*.jar'
                pattern 'ontrack-acceptance/build/libs/ontrack-acceptance.jar' // No version needed here
                pattern 'ontrack-dsl/build/libs/ontrack-dsl-*.jar'
                pattern 'ontrack-dsl/build/libs/ontrack-dsl-*.pom'
                pattern 'build/distributions/ontrack-*-delivery.zip'
            }
            tasks(
                    '**/*.java,**/*.groovy,**/*.xml,**/*.html,**/*.js',
                    '**/target/**,**/node_modules/**,**/vendor/**',
                    'FIXME', 'TODO', '@Deprecated', true
            )
            downstreamParameterized {
                trigger("${PROJECT}-${NAME}-acceptance-local", 'SUCCESS', false) {
                    propertiesFile('build/version.properties')
                }
            }
        }
        configure { node ->
            node / 'publishers' / 'net.nemerosa.ontrack.jenkins.OntrackBuildNotifier' {
                'project'('ontrack')
                'branch'(NAME)
                'build'('${VERSION_BUILD}')
            }
        }
    }

    // Local acceptance job

    freeStyleJob("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-local") {
        logRotator(numToKeep = 40)
        deliveryPipelineConfiguration('Commit', 'Local acceptance')
        jdk 'JDK8u25'
        parameters {
            stringParam('VERSION_FULL', '', '')
            stringParam('VERSION_COMMIT', '', '')
            stringParam('VERSION_BUILD', '', '')
            stringParam('VERSION_DISPLAY', '', '')
        }
        wrappers {
            xvfb('default')
        }
        extractDeliveryArtifacts delegate
        steps {
            // Runs the CI acceptance tests
            gradle """\
ciAcceptanceTest -PacceptanceJar=ontrack-acceptance.jar
"""
        }
        publishers {
            archiveJunit('ontrack-acceptance.xml')
            if (branchType == 'release') {
                downstreamParameterized {
                    trigger("${SEED_PROJECT}-${SEED_BRANCH}-docker-push", 'SUCCESS', false) {
                        currentBuild()
                    }
                }
            } else {
                buildPipelineTrigger("${SEED_PROJECT}/${SEED_PROJECT}-${SEED_BRANCH}/${PROJECT}-${NAME}-docker-push") {
                    parameters {
                        currentBuild()
                    }
                }
            }
        }
        configure { node ->
            node / 'publishers' / 'net.nemerosa.ontrack.jenkins.OntrackValidationRunNotifier' {
                'project'('ontrack')
                'branch'(NAME)
                'build'('${VERSION_BUILD}')
                'validationStamp'('ACCEPTANCE')
            }
        }
    }

    // Docker push

    freeStyleJob("${SEED_PROJECT}-${SEED_BRANCH}-docker-push") {
        logRotator(numToKeep = 40)
        deliveryPipelineConfiguration('Acceptance', 'Docker push')
        jdk 'JDK8u25'
        parameters {
            stringParam('VERSION_FULL', '', '')
            stringParam('VERSION_COMMIT', '', '')
            stringParam('VERSION_BUILD', '', '')
            stringParam('VERSION_DISPLAY', '', '')
        }
        wrappers {
            injectPasswords()
        }
        steps {
            shell """\
docker login --email="damien.coraboeuf+nemerosa@gmail.com" --username="nemerosa" --password="\${DOCKER_PASSWORD}"
docker push nemerosa/ontrack:\${VERSION_FULL}
docker logout
"""
        }
        publishers {
            downstreamParameterized {
                trigger("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-do", 'SUCCESS', false) {
                    currentBuild()
                }
            }
        }
        configure { node ->
            node / 'publishers' / 'net.nemerosa.ontrack.jenkins.OntrackValidationRunNotifier' {
                'project'('ontrack')
                'branch'(SEED_BRANCH)
                'build'('${VERSION_BUILD}')
                'validationStamp'('DOCKER')
            }
        }
    }

    // Digital Ocean acceptance job

    freeStyleJob("${PROJECT}-${NAME}-acceptance-do") {
        logRotator(numToKeep = 40)
        deliveryPipelineConfiguration('Acceptance', 'Digital Ocean')
        jdk 'JDK8u25'
        parameters {
            stringParam('VERSION_FULL', '', '')
            stringParam('VERSION_COMMIT', '', '')
            stringParam('VERSION_BUILD', '', '')
            stringParam('VERSION_DISPLAY', '', '')
        }
        wrappers {
            injectPasswords()
            xvfb('default')
        }
        extractDeliveryArtifacts delegate
        steps {
            // Runs the CI acceptance tests
            shell '''\
./gradlew \\
    doAcceptanceTest \\
    -PacceptanceJar=ontrack-acceptance.jar \\
    -PdigitalOceanAccessToken=${DO_TOKEN} \\
    -PontrackVersion=${VERSION_FULL}
'''
        }
        publishers {
            archiveJunit('ontrack-acceptance.xml')
            buildPipelineTrigger("${SEED_PROJECT}/${SEED_PROJECT}-${SEED_BRANCH}/${PROJECT}-${NAME}-publish") {
                parameters {
                    currentBuild()
                }
            }
        }
        configure { node ->
            node / 'publishers' / 'net.nemerosa.ontrack.jenkins.OntrackValidationRunNotifier' {
                'project'('ontrack')
                'branch'(NAME)
                'build'('${VERSION_BUILD}')
                'validationStamp'('ACCEPTANCE.DO')
            }
        }
    }

    // Publish job
    // Available for all branches, with some restrictions (no tagging) for non release branches
    boolean release = branchType == 'release'

    freeStyleJob("${PROJECT}-${NAME}-publish") {
        logRotator(numToKeep = 40)
        deliveryPipelineConfiguration('Release', 'Publish')
        jdk 'JDK8u25'
        parameters {
            stringParam('VERSION_FULL', '', '')
            stringParam('VERSION_COMMIT', '', '')
            stringParam('VERSION_BUILD', '', '')
            stringParam('VERSION_DISPLAY', '', '')
        }
        wrappers {
            injectPasswords()
            // toolenv('Maven-3.2.x')
        }
        extractDeliveryArtifacts delegate
        steps {
            // Publication
            if (release) {
                gradle '''\
-Ppublication
-PontrackVersion=${VERSION_DISPLAY}
publicationRelease
'''
            } else {
                gradle '''\
-Ppublication
-PontrackVersion=${VERSION_DISPLAY}
publicationMaven
'''
            }
//            if (release) {
//                shell readFileFromWorkspace('seed/publish-release.sh')
//                shell """\
//docker tag --force nemerosa/ontrack:\${VERSION_FULL} nemerosa/ontrack:latest
//docker tag --force nemerosa/ontrack:\${VERSION_FULL} nemerosa/ontrack:\${VERSION_DISPLAY}
//docker login --email="damien.coraboeuf+nemerosa@gmail.com" --username="nemerosa" --password="\${DOCKER_PASSWORD}"
//docker push nemerosa/ontrack:\${VERSION_DISPLAY}
//docker push nemerosa/ontrack:latest
//docker logout
//"""
//            } else {
//                shell readFileFromWorkspace('seed/publish.sh')
//            }
        }
        if (release) {
            publishers {
                buildPipelineTrigger("${SEED_PROJECT}/${SEED_PROJECT}-${SEED_BRANCH}/${SEED_PROJECT}-${SEED_BRANCH}-production") {
                    parameters {
                        currentBuild()
                    }
                }
            }
        }
        configure { node ->
            node / 'publishers' / 'net.nemerosa.ontrack.jenkins.OntrackPromotedRunNotifier' {
                'project'('ontrack')
                'branch'(NAME)
                'build'('${VERSION_BUILD}')
                'promotionLevel'('RELEASE')
            }
            node / 'publishers' / 'net.nemerosa.ontrack.jenkins.OntrackDSLNotifier' {
                'usingText' true
                'scriptText' """\
ontrack.build('ontrack', '${NAME}', VERSION_BUILD).config {
label VERSION_DISPLAY
}
"""
                injectEnvironment 'VERSION_BUILD,VERSION_DISPLAY'
                injectProperties ''
                ontrackLog false
            }
        }
    }

    if (release) {

        // Production deployment

        freeStyleJob("${PROJECT}-${NAME}-production") {
            logRotator(numToKeep = 40)
            deliveryPipelineConfiguration('Release', 'Production')
            jdk 'JDK8u25'
            parameters {
                stringParam('VERSION_FULL', '', '')
                stringParam('VERSION_COMMIT', '', '')
                stringParam('VERSION_BUILD', '', '')
                stringParam('VERSION_DISPLAY', '', '')
            }
            wrappers {
                injectPasswords()
            }
            steps {
                shell readFileFromWorkspace('seed/production.sh')
            }
            publishers {
                downstreamParameterized {
                    trigger("${PROJECT}-${NAME}-acceptance-production", 'SUCCESS', false) {
                        currentBuild()
                    }
                }
            }
            configure { node ->
                node / 'publishers' / 'net.nemerosa.ontrack.jenkins.OntrackPromotedRunNotifier' {
                    'project'('ontrack')
                    'branch'(NAME)
                    'build'('${VERSION_BUILD}')
                    'promotionLevel'('ONTRACK')
                }
            }
        }

        // Production acceptance test

        freeStyleJob("${PROJECT}-${NAME}-acceptance-production") {
            logRotator(numToKeep = 40)
            deliveryPipelineConfiguration('Release', 'Production acceptance')
            jdk 'JDK8u25'
            parameters {
                stringParam('VERSION_FULL', '', '')
                stringParam('VERSION_COMMIT', '', '')
                stringParam('VERSION_BUILD', '', '')
                stringParam('VERSION_DISPLAY', '', '')
            }
            wrappers {
                injectPasswords()
            }
            steps {
                shell readFileFromWorkspace('seed/production-acceptance.sh')
            }
            publishers {
                archiveJunit('ontrack-acceptance.xml')
            }
            configure { node ->
                node / 'buildWrappers' / 'org.jenkinsci.plugins.xvfb.XvfbBuildWrapper' {
                    'installationName'('default')
                    'screen'('1024x768x24')
                    'displayNameOffset'('1')
                }
                node / 'publishers' / 'net.nemerosa.ontrack.jenkins.OntrackValidationRunNotifier' {
                    'project'('ontrack')
                    'branch'(NAME)
                    'build'('${VERSION_BUILD}')
                    'validationStamp'('ONTRACK.SMOKE')
                }
            }
        }

    }

    // Pipeline view

    deliveryPipelineView('Pipeline') {
        pipelineInstances(4)
        enableManualTriggers()
        showChangeLog()
        updateInterval(5)
        pipelines {
            component("ontrack-${NAME}", "${PROJECT}-${NAME}-build")
        }
    }


} else {
    println "\tSkipping ${BRANCH}."
}
