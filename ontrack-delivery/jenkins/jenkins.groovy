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

String repository = 'nemerosa/ontrack'
String project = 'ontrack'

/**
 * Pipeline for a branch
 */
def generateBranch(String branchName) {
    // Normalised branch name
    def name = branchName.replaceAll(/[^A-Za-z0-9\.\-_]/, '-')
    // Folder for the branch
    folder {
        name "${project}/${project}-${name}"
    }
//    job {
//        name "${project}-${branchName}".replaceAll('/','-')
//        scm {
//            git("git://github.com/${project}.git", branchName)
//        }
//    }
}

/**
 * Generation for all branches
 */

String branchApi = new URL("https://api.github.com/repos/${repository}/branches")
def branches = new groovy.json.JsonSlurper().parse(branchApi.newReader())

branches.each {
    generateBranch it.name
}
