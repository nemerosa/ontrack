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
// Only release/3.x going to production
boolean production = release && BRANCH.startsWith('release/3')

/**
 * Extracting the delivery archive
 * @param modules List of modules to extract from the delivery / publication archuve
 */
def extractDeliveryArtifacts(Object dsl, String... modules) {
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
        // Checks the version (ZIP contains the VERSION parameter)
        // Expanding the delivery ZIP
        shell '''\
# Checks the version (ZIP contains the VERSION parameter)
if [ -f "ontrack-${VERSION}-delivery.zip" ]
then
   # Expanding the delivery ZIP
   unzip ontrack-${VERSION}-delivery.zip
else
   echo "Cannot find ontrack-${VERSION}-delivery.zip"
   exit 1
fi
'''
        // Injects the version
        environmentVariables {
            propertiesFile 'ontrack.properties'
        }
        // Extracting the publication archive
        shell 'unzip ontrack-publication.zip -d publication'
        // Extraction of modules
        if (modules && modules.length > 0) {
            // Moves the artifacts
            shell """${modules.collect{ "mv publication/${it}-\${VERSION}.jar ." }.join('\n')}"""
        }
    }
}

/**
 * Running a shell in a Xvfb session
 */

def withXvfb(def steps, String script) {
    steps.shell """\
#!/bin/bash

mkdir -p xvfb-\${EXECUTOR_NUMBER}-\${BUILD_NUMBER}
let 'NUM = EXECUTOR_NUMBER + 1'
echo "Display number: \${NUM}"
nohup /usr/bin/Xvfb :\${NUM} -screen 0 1024x768x24 -fbdir xvfb-\${EXECUTOR_NUMBER}-\${BUILD_NUMBER} & > xvfb.pid

# Make sure to stop Xvfb at the end
trap "kill -KILL `cat xvfb.pid`" EXIT

export DISPLAY=":\${NUM}"

${script}

# Exit normally in all cases
# Evaluation is done by test reporting
exit 0
"""
}

def inDocker (def job) {
    job.wrappers {
        buildInDocker {
            dockerfile('seed/docker')
            volume '/var/run/docker.sock', '/var/run/docker.sock'
        }
    }
}

def preparePipelineJob(def job, boolean acceptance = true) {
    job.parameters {
        // Link based on full version
        stringParam('VERSION', '', '')
        // ... and Git commit
        stringParam('COMMIT', '', '')
    }
    job.label 'docker'
    job.scm {
        git {
            remote {
                url PROJECT_SCM_URL
                branch '${COMMIT}'
            }
            extensions {
                wipeOutWorkspace()
                localBranch "${BRANCH}"
            }
        }
    }
    inDocker job
    extractDeliveryArtifacts job, acceptance ? ['ontrack-acceptance'] as String[] : [] as String[]
}

// CentOS versions to tests
List<String> centOsVersions = [
        '7',
        '6',
]

// Build job
job("${SEED_PROJECT}-${SEED_BRANCH}-build") {
    logRotator {
        numToKeep(40)
        artifactNumToKeep(5)
    }
    label 'docker'
    inDocker delegate
    deliveryPipelineConfiguration('Commit', 'Build')
    scm {
        git {
            remote {
                url PROJECT_SCM_URL
                branch "origin/${BRANCH}"
            }
            extensions {
                wipeOutWorkspace()
                localBranch "${BRANCH}"
            }
        }
    }
    steps {
        gradle '''\
clean
versionDisplay
versionFile
test
integrationTest
dockerLatest
osPackages
build
-Pdocumentation
-PbowerOptions='--allow-root'
-Dorg.gradle.jvmargs=-Xmx1536m
--info
--stacktrace
--profile
--console plain
'''
        environmentVariables {
            propertiesFile 'build/version.properties'
        }
    }
    publishers {
        buildDescription '', '${VERSION_DISPLAY}', '', ''
        archiveJunit("**/build/test-results/**/*.xml")
        archiveArtifacts {
            pattern 'build/distributions/ontrack-*-delivery.zip'
            pattern 'build/distributions/ontrack*.deb'
            pattern 'build/distributions/ontrack*.rpm'
        }
        tasks(
                '**/*.java,**/*.groovy,**/*.xml,**/*.html,**/*.js',
                '**/build/**,**/node_modules/**,**/vendor/**',
                'FIXME', 'TODO', '@Deprecated', true
        )
        downstreamParameterized {
            trigger("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-local") {
                condition('SUCCESS')
                parameters {
                    // Link based on full version
                    predefinedProp 'VERSION', '${VERSION_DISPLAY}'
                    // Git
                    predefinedProp 'COMMIT', '${VERSION_COMMIT}'
                    // Uses the same node in order to have local Docker image available
                    sameNode()
                }
            }
        }
        // Use display version & Git commit
        ontrackDsl {
            log()
            environment 'VERSION_DISPLAY'
            environment 'GIT_COMMIT'
            script """\
def build = ontrack.branch('${SEED_PROJECT}', '${SEED_BRANCH}').build(VERSION_DISPLAY, '', true)
build.config.gitCommit GIT_COMMIT
"""

        }
    }
}

// Local acceptance job

job("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-local") {
    logRotator {
        numToKeep(40)
        artifactNumToKeep(5)
    }
    deliveryPipelineConfiguration('Commit', 'Local acceptance')
    preparePipelineJob delegate
    steps {
        // Runs Xfvb in the background - it will be killed when the Docker slave is removed
        // Runs the CI acceptance tests
        withXvfb delegate, '''\
./gradlew \\
    ciAcceptanceTest \\
    -PacceptanceJar=ontrack-acceptance-${VERSION}.jar \\
    -PciHost=dockerhost \\
    -Dorg.gradle.jvmargs=-Xmx1536m \\
    --info \\
    --profile \\
    --console plain \\
    --stacktrace
'''
    }
    publishers {
        buildDescription '', '${VERSION}', '', ''
        archiveJunit('build/acceptance/*.xml')
        if (release) {
            downstreamParameterized {
                trigger("${SEED_PROJECT}-${SEED_BRANCH}-docker-push") {
                    condition 'SUCCESS'
                    parameters {
                        currentBuild()
                        // Uses the same node in order to have local Docker image available
                        sameNode()
                    }
                }
            }
        } else {
            buildPipelineTrigger("${SEED_PROJECT}/${SEED_PROJECT}-${SEED_BRANCH}/${SEED_PROJECT}-${SEED_BRANCH}-docker-push") {
                parameters {
                    currentBuild()
                    // Uses the same node in order to have local Docker image available
                    sameNode()
                }
            }
        }
        // Use display version
        ontrackValidation SEED_PROJECT, SEED_BRANCH, '${VERSION}', 'ACCEPTANCE'
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
        deliveryPipelineConfiguration('Acceptance', 'Debian package acceptance')
        preparePipelineJob delegate
        steps {
            // Runs the CI acceptance tests
            withXvfb delegate, """\
./gradlew \\
    debAcceptanceTest \\
    -PacceptanceJar=ontrack-acceptance-\${VERSION}.jar \\
    -PacceptanceDebianDistributionDir=. \\
    -PacceptanceHost=dockerhost \\
    -Dorg.gradle.jvmargs=-Xmx1536m \\
    --info \\
    --profile \\
    --console plain \\
    --stacktrace
"""
        }
        publishers {
            archiveJunit('build/acceptance/*.xml')
            // Use display version
            ontrackValidation SEED_PROJECT, SEED_BRANCH, '${VERSION_DISPLAY}', 'ACCEPTANCE.DEBIAN'
        }
    }

    // CentOS package acceptance job

    centOsVersions.each { String centOsVersion ->
        job("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-centos-${centOsVersion}") {
            logRotator {
                numToKeep(40)
                artifactNumToKeep(5)
            }
            deliveryPipelineConfiguration('Acceptance', "CentOS ${centOsVersion} package acceptance")
            preparePipelineJob delegate
            steps {
                // Runs the CI acceptance tests
                withXvfb delegate, """\
./gradlew \\
    rpmAcceptanceTest${centOsVersion} \\
    -PacceptanceJar=ontrack-acceptance-\${VERSION}.jar \\
    -PacceptanceRpmDistributionDir=. \\
    -PacceptanceHost=dockerhost \\
    -Dorg.gradle.jvmargs=-Xmx1536m \\
    --info \\
    --profile \\
    --console plain \\
    --stacktrace \\
"""
            }
            publishers {
                archiveJunit('build/acceptance/*.xml')
                ontrackValidation SEED_PROJECT, SEED_BRANCH, '${VERSION_DISPLAY}', "ACCEPTANCE.CENTOS.${centOsVersion}" as String
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
    preparePipelineJob delegate
    wrappers {
        injectPasswords {
            injectGlobalPasswords()
        }
    }
    steps {
        shell """\
docker login --email="damien.coraboeuf+nemerosa@gmail.com" --username="nemerosa" --password="\${DOCKER_PASSWORD}"
docker push nemerosa/ontrack:\${VERSION}
docker logout
"""
    }
    publishers {
        downstreamParameterized {
            trigger("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-do") {
                condition('SUCCESS')
                parameters {
                    currentBuild() // VERSION
                }
            }
        }
        if (release) {
            downstreamParameterized {
                trigger("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-debian") {
                    condition 'SUCCESS'
                    parameters {
                        currentBuild()
                    }
                }
                centOsVersions.each { centOsVersion ->
                    trigger("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-centos-${centOsVersion}") {
                        condition 'SUCCESS'
                        parameters {
                            currentBuild()
                        }
                    }
                }
            }
        }
        // Use display version
        ontrackValidation SEED_PROJECT, SEED_BRANCH, '${VERSION}', 'DOCKER'
    }
}

// Digital Ocean acceptance job

job("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-do") {
    logRotator {
        numToKeep(40)
        artifactNumToKeep(5)
    }
    deliveryPipelineConfiguration('Acceptance', 'Digital Ocean')
    preparePipelineJob delegate
    wrappers {
        injectPasswords {
            injectGlobalPasswords()
        }
    }
    steps {
        // Runs Xfvb in the background - it will be killed when the Docker slave is removed
        // Runs the CI acceptance tests
        withXvfb delegate, '''\
./gradlew \\
    doAcceptanceTest \\
    -PacceptanceJar=ontrack-acceptance-${VERSION}.jar \\
    -PontrackVersion=${VERSION} \\
    -PdigitalOceanAccessToken=${DO_TOKEN} \\
    -Dorg.gradle.jvmargs=-Xmx1536m \\
    --info \\
    --profile \\
    --console plain \\
    --stacktrace
'''

    }
    publishers {
        archiveJunit('build/acceptance/*.xml')
        buildPipelineTrigger("${SEED_PROJECT}/${SEED_PROJECT}-${SEED_BRANCH}/${SEED_PROJECT}-${SEED_BRANCH}-publish") {
            parameters {
                currentBuild()
            }
        }
        ontrackValidation SEED_PROJECT, SEED_BRANCH, '${VERSION}', 'ACCEPTANCE.DO'
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
    preparePipelineJob delegate, false
    wrappers {
        credentialsBinding {
            file 'GPG_KEY_FILE', 'GPGKeyRing'
        }
        injectPasswords {
            injectGlobalPasswords()
        }
    }
    steps {
        // Publication
        if (release) {
            gradle """\
--build-file publication.gradle
--info
--profile
--console plain
--stacktrace
-PontrackVersion=\${VERSION}
-PontrackVersionCommit=\${COMMIT}
-PontrackReleaseBranch=${SEED_BRANCH}
-Psigning.keyId=\${GPG_KEY_ID}
-Psigning.password=\${GPG_KEY_PASSWORD}
-Psigning.secretKeyRingFile=\${GPG_KEY_FILE}
-PossrhUser=\${OSSRH_USER}
-PossrhPassword=\${OSSRH_PASSWORD}
-PgitHubUser=dcoraboeuf
-PgitHubPassword=\${GITHUB_TOKEN}
publicationRelease
"""
        } else {
            gradle """\
--build-file publication.gradle
--info
--profile
--console plain
--stacktrace
-PontrackVersion=\${VERSION}
-PontrackVersionCommit=\${VERSION}
-PontrackReleaseBranch=${SEED_BRANCH}
-Psigning.keyId=\${GPG_KEY_ID}
-Psigning.password=\${GPG_KEY_PASSWORD}
-Psigning.secretKeyRingFile=\${GPG_KEY_FILE}
-PossrhUser=\${OSSRH_USER}
-PossrhPassword=\${OSSRH_PASSWORD}
publicationMaven
"""
        }
        if (release) {
            shell """\
docker pull nemerosa/ontrack:\${VERSION}
docker tag nemerosa/ontrack:\${VERSION} nemerosa/ontrack:latest
docker login --email="damien.coraboeuf+nemerosa@gmail.com" --username="nemerosa" --password="\${DOCKER_PASSWORD}"
docker push nemerosa/ontrack:\${VERSION}
docker push nemerosa/ontrack:latest
docker logout
"""
        }
    }
    if (production) {
        publishers {
            buildPipelineTrigger("${SEED_PROJECT}/${SEED_PROJECT}-${SEED_BRANCH}/${SEED_PROJECT}-${SEED_BRANCH}-production") {
                parameters {
                    currentBuild() // VERSION
                }
            }
        }
    }
    publishers {
        downstreamParameterized {
            trigger("${SEED_PROJECT}-${SEED_BRANCH}-site") {
                condition('SUCCESS')
                parameters {
                    currentBuild() // VERSION
                }
            }
        }
    }
    publishers {
        // Use display version
        ontrackPromotion SEED_PROJECT, SEED_BRANCH, '${VERSION}', 'RELEASE'
        // Use display version
        ontrackDsl {
            environment 'VERSION'
            log()
            script """\
ontrack.build('${SEED_PROJECT}', '${SEED_BRANCH}', VERSION).config {
    label VERSION
}
"""
        }
    }
}

// Site job

job("${SEED_PROJECT}-${SEED_BRANCH}-site") {
    logRotator {
        numToKeep(40)
        artifactNumToKeep(5)
    }
    deliveryPipelineConfiguration('Release', 'Site')
    preparePipelineJob delegate, false
    wrappers {
        injectPasswords {
            injectGlobalPasswords()
        }
    }
    steps {
            gradle """\
--build-file site.gradle
--info
--profile
--stacktrace
-PontrackVersion=\${VERSION}
-PontrackGitHubUri=${PROJECT_SCM_URL}
-PontrackGitHubPages=gh-pages
-PontrackGitHubUser=\${GITHUB_USER}
-PontrackGitHubPassword=\${GITHUB_TOKEN}
site
"""
    }
    publishers {
        // Use display version
        ontrackValidation SEED_PROJECT, SEED_BRANCH, '${VERSION}', 'SITE'
    }
}

if (production) {

    // Production deployment

    job("${SEED_PROJECT}-${SEED_BRANCH}-production") {
        logRotator {
            numToKeep(40)
            artifactNumToKeep(5)
        }
        deliveryPipelineConfiguration('Release', 'Production')
        preparePipelineJob delegate, false
        wrappers {
            injectPasswords {
                injectGlobalPasswords()
            }
        }
        steps {
            gradle '''\
--build-file production.gradle
--info
--profile
--console plain
--stacktrace
productionUpgrade
-PontrackVersion=${VERSION_DISPLAY}
'''
        }
        publishers {
            archiveArtifacts {
                pattern 'build/*.tgz'
            }
            downstreamParameterized {
                trigger("${SEED_PROJECT}-${SEED_BRANCH}-acceptance-production") {
                    condition 'SUCCESS'
                    parameters {
                        currentBuild()
                    }
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
        preparePipelineJob delegate
        wrappers {
            injectPasswords {
                injectGlobalPasswords()
            }
        }
        steps {
            gradle '''\
--build-file production.gradle
--info
--profile
--console plain
--stacktrace
productionTest
-Dorg.gradle.jvmargs=-Xmx1536m \\
-PacceptanceJar=ontrack-acceptance-${VERSION}.jar
'''
        }
        publishers {
            archiveJunit('build/acceptance/*.xml')
            ontrackValidation SEED_PROJECT, SEED_BRANCH, '${VERSION_DISPLAY}', 'ONTRACK.SMOKE'
            ontrackPromotion SEED_PROJECT, SEED_BRANCH, '${VERSION_DISPLAY}', 'ONTRACK'
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
    label 'master'
    wrappers {
        injectPasswords {
            injectGlobalPasswords()
        }
    }
    steps {
        ontrackDsl {
            log()
            script """\
ontrack.project('${SEED_PROJECT}').branch('${SEED_BRANCH}', 'Pipeline for ${BRANCH}', true).config {
    gitBranch '${BRANCH}', [
        buildCommitLink: [
            id: 'git-commit-property'
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
