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
boolean release = branchType == 'release'

// Extracting the delivery
def extractDeliveryArtifacts(Object dsl) {
    dsl.steps {
        // Cleaning the workspace
        shell 'rm -rf ${WORKSPACE}'
        // Copy of artifacts
        copyArtifacts("${SEED_PROJECT}-${SEED_BRANCH}-build") {
            flatten()
            buildSelector {
                upstreamBuild(true)
            }
        }
        // Expanding the delivery ZIP
        shell 'unzip ontrack-*-delivery.zip'
    }
}

// CentOS versions to tests
def centOsVersions = [
        '7',
        '6',
]

// Build job
job("${SEED_PROJECT}-${SEED_BRANCH}-build") {
    logRotator {
        numToKeep(40)
        artifactNumToKeep(5)
    }
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
        // TODO Restore parallel build when https://github.com/srs/gradle-node-plugin/issues/91 is fixed
        gradle '''\
clean
versionDisplay
versionFile
test
integrationTest
dockerLatest
osPackages
build
publicationPackage
--info
--profile
'''
        environmentVariables {
            propertiesFile 'build/version.properties'
        }
    }
    publishers {
        archiveJunit("**/build/test-results/*.xml")
        archiveArtifacts {
            pattern 'ontrack-ui/build/libs/ontrack-ui-*.jar'
            pattern 'ontrack-acceptance/build/libs/ontrack-acceptance.jar' // No version needed here
            pattern 'build/distributions/ontrack-*-publication.zip'
            pattern 'build/distributions/ontrack-*-delivery.zip'
            pattern 'build/distributions/ontrack*.deb'
            pattern 'build/distributions/ontrack*.rpm'
        }
        tasks(
                '**/*.java,**/*.groovy,**/*.xml,**/*.html,**/*.js',
                '**/target/**,**/node_modules/**,**/vendor/**',
                'FIXME', 'TODO', '@Deprecated', true
        )
        downstreamParameterized {
            trigger("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-local") {
                condition('SUCCESS')
                parameters {
                    propertiesFile('build/version.properties')
                }
            }
        }
        ontrackBuild SEED_PROJECT, SEED_BRANCH, '${VERSION_BUILD}'
    }
}

// Local acceptance job

job("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-local") {
    logRotator {
        numToKeep(40)
        artifactNumToKeep(5)
    }
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
        archiveJunit('*-tests.xml')
        if (release) {
            downstreamParameterized {
                trigger("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-debian", 'SUCCESS', false) {
                    currentBuild()
                }
                centOsVersions.each { centOsVersion ->
                    trigger("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-centos-${centOsVersion}", 'SUCCESS', false) {
                        currentBuild()
                    }
                }
            }
        }
        if (release) {
            downstreamParameterized {
                trigger("${SEED_PROJECT}-${SEED_BRANCH}-docker-push", 'SUCCESS', false) {
                    currentBuild()
                }
            }
        } else {
            buildPipelineTrigger("${SEED_PROJECT}/${SEED_PROJECT}-${SEED_BRANCH}/${SEED_PROJECT}-${SEED_BRANCH}-docker-push") {
                parameters {
                    currentBuild()
                }
            }
        }
        ontrackValidation SEED_PROJECT, SEED_BRANCH, '${VERSION_BUILD}', 'ACCEPTANCE'
    }
}

// OS packages jobs
// Only for releases

if (release) {

    // Debian package acceptance job

    job("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-debian") {
        logRotator {
            numToKeep(40)
            artifactNumToKeep(5)
        }
        deliveryPipelineConfiguration('Commit', 'Debian package acceptance')
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
debAcceptanceTest
-PacceptanceJar=ontrack-acceptance.jar
-PacceptanceDebianDistributionDir=.
"""
        }
        publishers {
            archiveJunit('*-tests.xml')
            ontrackValidation SEED_PROJECT, SEED_BRANCH, '${VERSION_BUILD}', 'ACCEPTANCE.DEBIAN'
        }
    }

    // CentOS package acceptance job

    centOsVersions.each { centOsVersion ->
        job("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-centos-${centOsVersion}") {
            logRotator {
                numToKeep(40)
                artifactNumToKeep(5)
            }
            deliveryPipelineConfiguration('Commit', "CentOS ${centOsVersion} package acceptance")
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
rpmAcceptanceTest${centOsVersion}
-PacceptanceJar=ontrack-acceptance.jar
-PacceptanceRpmDistributionDir=.
"""
            }
            publishers {
                archiveJunit('*-tests.xml')
                ontrackValidation SEED_PROJECT, SEED_BRANCH, '${VERSION_BUILD}', "ACCEPTANCE.CENTOS.${centOsVersion}"
            }
        }
    }

}

// Docker push

job("${SEED_PROJECT}-${SEED_BRANCH}-docker-push") {
    logRotator {
        numToKeep(40)
        artifactNumToKeep(5)
    }
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
            trigger("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-do") {
                condition('SUCCESS')
                parameters {
                    currentBuild()
                }
            }
        }
        ontrackValidation SEED_PROJECT, SEED_BRANCH, '${VERSION_BUILD}', 'DOCKER'
    }
}

// Digital Ocean acceptance job

job("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-do") {
    logRotator {
        numToKeep(40)
        artifactNumToKeep(5)
    }
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
        archiveJunit('*-tests.xml')
        buildPipelineTrigger("${SEED_PROJECT}/${SEED_PROJECT}-${SEED_BRANCH}/${SEED_PROJECT}-${SEED_BRANCH}-publish") {
            parameters {
                currentBuild()
            }
        }
        ontrackValidation SEED_PROJECT, SEED_BRANCH, '${VERSION_BUILD}', 'ACCEPTANCE.DO'
    }
}

// Publish job
// Available for all branches, with some restrictions (no tagging) for non release branches

job("${SEED_PROJECT}-${SEED_BRANCH}-publish") {
    logRotator {
        numToKeep(40)
        artifactNumToKeep(5)
    }
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
    }
    extractDeliveryArtifacts delegate
    steps {
        // Publication
        if (release) {
            gradle """\
--build-file publication.gradle
--info
--profile
--stacktrace
-Ppublication
-PontrackVersion=\${VERSION_DISPLAY}
-PontrackVersionCommit=\${VERSION_COMMIT}
-PontrackVersionFull=\${VERSION_FULL}
-PontrackReleaseBranch=${SEED_BRANCH}
publicationRelease
"""
        } else {
            gradle """\
--build-file publication.gradle
--info
--profile
--stacktrace
-Ppublication
-PontrackVersion=\${VERSION_DISPLAY}
-PontrackVersionCommit=\${VERSION_COMMIT}
-PontrackVersionFull=\${VERSION_FULL}
-PontrackReleaseBranch=${SEED_BRANCH}
publicationMaven
"""
        }
        if (release) {
            shell """\
docker tag --force nemerosa/ontrack:\${VERSION_FULL} nemerosa/ontrack:latest
docker tag --force nemerosa/ontrack:\${VERSION_FULL} nemerosa/ontrack:\${VERSION_DISPLAY}
docker login --email="damien.coraboeuf+nemerosa@gmail.com" --username="nemerosa" --password="\${DOCKER_PASSWORD}"
docker push nemerosa/ontrack:\${VERSION_DISPLAY}
docker push nemerosa/ontrack:latest
docker logout
"""
        }
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
    publishers {
        ontrackPromotion SEED_PROJECT, SEED_BRANCH, '${VERSION_BUILD}', 'RELEASE'
        ontrackDsl {
            environment 'VERSION_BUILD'
            environment 'VERSION_DISPLAY'
            log()
            script """\
ontrack.build('${SEED_PROJECT}', '${SEED_BRANCH}', VERSION_BUILD).config {
label VERSION_DISPLAY
}
"""
        }
    }
}

if (release) {

    // Production deployment

    job("${SEED_PROJECT}-${SEED_BRANCH}-production") {
        logRotator {
            numToKeep(40)
            artifactNumToKeep(5)
        }
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
            xvfb('default')
        }
        extractDeliveryArtifacts delegate
        steps {
            gradle '''\
--build-file production.gradle
--info
--profile
-Ppublication
productionUpgrade
-PontrackVersion=${VERSION_DISPLAY}
'''
        }
        publishers {
            archiveArtifacts {
                pattern 'build/*.tgz'
            }
            downstreamParameterized {
                trigger("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-production", 'SUCCESS', false) {
                    currentBuild()
                }
            }
        }
    }

    // Production acceptance test

    job("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-production") {
        logRotator {
            numToKeep(40)
            artifactNumToKeep(5)
        }
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
            xvfb('default')
        }
        extractDeliveryArtifacts delegate
        steps {
            gradle '''\
--build-file production.gradle
--info
--profile
-Ppublication
productionTest
-PacceptanceJar=ontrack-acceptance.jar
'''
        }
        publishers {
            archiveJunit('*-tests.xml')
            ontrackValidation SEED_PROJECT, SEED_BRANCH, '${VERSION_BUILD}', 'ONTRACK.SMOKE'
            ontrackPromotion SEED_PROJECT, SEED_BRANCH, '${VERSION_BUILD}', 'ONTRACK'
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
        component("ontrack-${SEED_BRANCH}", "${SEED_PROJECT}-${SEED_BRANCH}-build")
    }
}

// Setup


job("${SEED_PROJECT}-${SEED_BRANCH}-setup") {
    logRotator {
        numToKeep(40)
        artifactNumToKeep(1)
    }
    jdk 'JDK8u25'
    wrappers {
        injectPasswords()
    }
    steps {
        ontrackDsl {
            log()
            script """\
ontrack.project('${SEED_PROJECT}').branch('${SEED_BRANCH}', 'Pipeline for ${BRANCH}', true).config {
    gitBranch '${BRANCH}', [
        buildCommitLink: [
            id: 'commit',
            data: [
                abbreviated: true
            ]
        ]
    ]
}
"""
        }
    }
    publishers {
        downstreamParameterized {
            // Explicitly fires the build after setup
            // The global Seed property `pipeline-start-auto` has been set to `no`
            trigger("${SEED_PROJECT}-${SEED_BRANCH}-build") {
                condition('SUCCESS')
                triggerWithNoParameters()
            }
        }
    }
}

// Fires the queue job upon generation

queue("${SEED_PROJECT}-${SEED_BRANCH}-setup")
