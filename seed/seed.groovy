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
 * List of parameters (see https://github.com/nemerosa/seed):
 *
 * - PROJECT
 * - BRANCH
 * - SCM_URL
 */

/**
 * Variables
 */

def LOCAL_REPOSITORY = '/var/lib/jenkins/repository/ontrack/2.0'

/**
 * Folder for the project (making sure)
 */

folder {
    name PROJECT
}

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

// Keeps only some version types
if (['master', 'feature', 'release', 'hotfix'].contains(branchType)) {

    // Normalised branch name
    def NAME = BRANCH.replaceAll(/[^A-Za-z0-9\.\-_]/, '-')
    println "\tGenerating ${NAME}..."

    // Folder for the branch
    folder {
        name "${PROJECT}/${PROJECT}-${NAME}"
    }

    // Build job
    job {
        name "${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-build"
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
        triggers {
            scm 'H/5 * * * *'
        }
        steps {
            gradle 'clean versionDisplay versionFile test integrationTest release  --info --profile --parallel'
            conditionalSteps {
                condition {
                    status('SUCCESS', 'SUCCESS')
                }
                runner('Fail')
                shell """\
# Copies the JAR to a local directory
ontrack-delivery/archive.sh --source=\${WORKSPACE} --destination=${LOCAL_REPOSITORY}
"""
            }
            environmentVariables {
                propertiesFile 'build/version.properties'
            }
        }
        publishers {
            archiveJunit("**/build/test-results/*.xml")
            tasks(
                    '**/*.java,**/*.groovy,**/*.xml,**/*.html,**/*.js',
                    '**/target/**,**/node_modules/**,**/vendor/**',
                    'FIXME', 'TODO', '@Deprecated', true
            )
            downstreamParameterized {
                trigger("${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-acceptance-local", 'SUCCESS', false) {
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

    job {
        name "${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-acceptance-local"
        logRotator(numToKeep = 40)
        deliveryPipelineConfiguration('Commit', 'Local acceptance')
        jdk 'JDK8u25'
        parameters {
            stringParam('VERSION_FULL', '', '')
            stringParam('VERSION_COMMIT', '', '')
            stringParam('VERSION_BUILD', '', '')
            stringParam('VERSION_DISPLAY', '', '')
        }
        steps {
            shell readFileFromWorkspace('seed/local-acceptance.sh')
        }
        publishers {
            archiveJunit('ontrack-acceptance.xml')
            if (branchType == 'release') {
                downstreamParameterized {
                    trigger("${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-docker-push", 'SUCCESS', false) {
                        currentBuild()
                    }
                }
            } else {
                buildPipelineTrigger("${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-docker-push") {
                    parameters {
                        currentBuild()
                    }
                }
            }
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
                'validationStamp'('ACCEPTANCE')
            }
        }
    }

    // Docker push

    job {
        name "${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-docker-push"
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
docker tag ontrack:\${VERSION_FULL} nemerosa/ontrack:\${VERSION_FULL}
docker login --email="damien.coraboeuf+nemerosa@gmail.com" --username="nemerosa" --password="\${DOCKER_PASSWORD}"
docker push nemerosa/ontrack:\${VERSION_FULL}
docker logout
"""
        }
        publishers {
            downstreamParameterized {
                trigger("${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-acceptance-do", 'SUCCESS', false) {
                    currentBuild()
                }
            }
        }
        configure { node ->
            node / 'publishers' / 'net.nemerosa.ontrack.jenkins.OntrackValidationRunNotifier' {
                'project'('ontrack')
                'branch'(NAME)
                'build'('${VERSION_BUILD}')
                'validationStamp'('DOCKER')
            }
        }
    }

    // Digital Ocean acceptance job

    job {
        name "${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-acceptance-do"
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
        }
        steps {
            shell readFileFromWorkspace('seed/do-acceptance.sh')
        }
        publishers {
            archiveJunit('ontrack-acceptance.xml')
            if (branchType == 'release') {
                buildPipelineTrigger("${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-21-publish") {
                    parameters {
                        currentBuild()
                    }
                }
            }
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
                'validationStamp'('ACCEPTANCE.DO')
            }
        }
    }

    if (branchType == 'release') {

        // Publish job

        job {
            name "${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-publish"
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
                toolenv('Maven-3.2.x')
            }
            steps {
                environmentVariables {
                    env 'VERSION_BRANCHID', NAME
                }
                shell readFileFromWorkspace('seed/publish.sh')
                shell """\
docker tag nemerosa/ontrack:\${VERSION_FULL} nemerosa/ontrack:latest
docker tag nemerosa/ontrack:\${VERSION_FULL} nemerosa/ontrack:\${VERSION_DISPLAY}
docker login --email="damien.coraboeuf+nemerosa@gmail.com" --username="nemerosa" --password="\${DOCKER_PASSWORD}"
docker push nemerosa/ontrack:\${VERSION_DISPLAY}
docker push nemerosa/ontrack:latest
docker logout
"""
            }
            publishers {
                buildPipelineTrigger("${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-production") {
                    parameters {
                        currentBuild()
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

        // Production deployment

        job {
            name "${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-production"
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
                    trigger("${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-acceptance-production", 'SUCCESS', false) {
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

        job {
            name "${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-acceptance-production"
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

    view(type: DeliveryPipelineView) {
        name "${PROJECT}/${PROJECT}-${NAME}/Pipeline"
        pipelineInstances(4)
        enableManualTriggers()
        showChangeLog()
        updateInterval(5)
        pipelines {
            component("ontrack-${NAME}", "${PROJECT}/${PROJECT}-${NAME}/${PROJECT}-${NAME}-build")
        }
    }


} else {
    println "\tSkipping ${BRANCH}."
}
