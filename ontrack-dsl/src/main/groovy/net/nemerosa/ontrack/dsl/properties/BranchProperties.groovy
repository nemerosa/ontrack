package net.nemerosa.ontrack.dsl.properties

import net.nemerosa.ontrack.dsl.Branch
import net.nemerosa.ontrack.dsl.Ontrack

class BranchProperties extends ProjectEntityProperties {

    BranchProperties(Ontrack ontrack, Branch branch) {
        super(ontrack, branch)
    }

    /**
     * Git branch property
     */
    def gitBranch(String branch, Map<String, ?> params = [:]) {
        property(
                'net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType',
                [branch: branch] + params
        )
    }

    /**
     * Access to the Git branch property
     */
    def getGitBranch() {
        property('net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType')
    }

    /**
     * SVN revision change log issue validator
     */
    def svnValidatorClosedIssues(Collection<String> closedStatuses) {
        property('net.nemerosa.ontrack.extension.svn.property.SVNRevisionChangeLogIssueValidator', [
                closedStatuses: closedStatuses
        ])
    }

    /**
     * SVN revision change log issue validator
     */
    def getSvnValidatorClosedIssues() {
        property('net.nemerosa.ontrack.extension.svn.property.SVNRevisionChangeLogIssueValidator')
    }

}
