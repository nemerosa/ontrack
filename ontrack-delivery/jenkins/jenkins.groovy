/**
 * List of global parameters:
 *
 * - JDK8u20
 *
 * List of plug-ins:
 *
 * - Delivery pipeline
 * - Build pipeline
 * - Parameterized trigger
 * - Git
 * - Folders
 */

/**
 * Variables
 */

def REPOSITORY = 'nemerosa/ontrack'
def PROJECT = 'ontrack'

/**
 * Folder for the project (making sure)
 */

folder {
    name PROJECT
}

/**
 * Generation for all branches
 */

URL branchApi = new URL("https://api.github.com/repos/${REPOSITORY}/branches")
def branches = new groovy.json.JsonSlurper().parse(branchApi.newReader())

branches.each {
    def BRANCH = it.name
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
    } else {
        println "\tSkipping."
    }
}
