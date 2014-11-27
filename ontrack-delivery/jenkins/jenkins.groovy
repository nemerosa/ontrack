/**
 * List of global parameters:
 *
 * - JDK8u20
 */

/**
 * Trigger job
 */

job {
    name 'ontrack-2-trigger'
    logRotator(numToKeep = 40)
    scm {
        git {
            remote {
                url 'git@github.com:nemerosa/ontrack.git'
            }
            branch '*/feature/*'
            branch '*/release/*'
            branch '*/hotfix/*'
            branch '*/master'
        }
    }
    triggers {
        scm 'H/5 * * * *'
    }
    wrappers {
        buildName '${GIT_BRANCH}'
    }
    steps {
        shell """\
echo \$GIT_BRANCH
LOCAL_BRANCH=`echo \$GIT_BRANCH | sed 's|origin/||'`
rm -f branch.properties
echo LOCAL_BRANCH=\$LOCAL_BRANCH >> branch.properties
echo REMOTE_BRANCH=\$GIT_BRANCH >> branch.properties
"""
    }
    publishers {
        downstreamParameterized {
            trigger('ontrack-2-quick') {
                gitRevision(true)
                propertiesFile('branch.properties')
            }
        }
    }
}

/**
 * Commit phase
 */

job {
    name 'ontrack-2-quick'
    logRotator(numToKeep = 40)
    deliveryPipelineConfiguration('Commit', 'Quick check')
    parameters {
        stringParam('LOCAL_BRANCH', 'master', '')
        stringParam('REMOTE_BRANCH', 'origin/master', '')
    }
    jdk 'JDK8u20'
    scm {
        git {
            remote {
                url 'git@github.com:nemerosa/ontrack.git'
            }
            branch '${REMOTE_BRANCH}'
            localBranch '${LOCAL_BRANCH}'
        }
    }
    wrappers {
        buildName '${ENV,var="LOCAL_BRANCH"}'
    }
    steps {
        gradle """\
displayVersion writeVersion test --info
"""
    }
    publishers {
        downstreamParameterized {
            trigger('ontrack-2-package') {
                currentBuild()
            }
        }
    }
}

job {
    name 'ontrack-2-package'
    logRotator(numToKeep = 40)
    deliveryPipelineConfiguration('Commit', 'Package')
    parameters {
        stringParam('LOCAL_BRANCH', 'master', '')
        stringParam('REMOTE_BRANCH', 'origin/master', '')
    }
    jdk 'JDK8u20'
    scm {
        git {
            remote {
                url 'git@github.com:nemerosa/ontrack.git'
            }
            branch '${REMOTE_BRANCH}'
            localBranch '${LOCAL_BRANCH}'
        }
    }
    wrappers {
        buildName '${ENV,var="LOCAL_BRANCH"}'
    }
    // TODO Xvfb
    steps {
        gradle """\
clean
displayVersion
writeVersion
test
release
integrationTest
--info
--profile
"""
    }
}

/**
 * Acceptance phase
 */

job {
    name 'ontrack-2-acceptance-local'
    logRotator(numToKeep = 40)
    deliveryPipelineConfiguration('Acceptance', 'Local acceptance')
}

job {
    name 'ontrack-2-docker'
    logRotator(numToKeep = 40)
    deliveryPipelineConfiguration('Acceptance', 'Docker publication')
}

job {
    name 'ontrack-2-acceptance-do'
    logRotator(numToKeep = 40)
    deliveryPipelineConfiguration('Acceptance', 'Digital Ocean acceptance')
}

/**
 * Release phase
 */

job {
    name 'ontrack-2-publish'
    logRotator(numToKeep = 40)
    deliveryPipelineConfiguration('Release', 'Publication')
}

job {
    name 'ontrack-2-production'
    logRotator(numToKeep = 40)
    deliveryPipelineConfiguration('Release', 'Production deployment')
}
