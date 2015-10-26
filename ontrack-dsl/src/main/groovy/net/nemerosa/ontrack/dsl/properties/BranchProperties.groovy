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

    def getGitBranch() {
        property('net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType')
    }

    /**
     * SVN branch property
     */

    @Deprecated
    def svn(String branchPath, String buildPath) {

    }

    def svn(Map<String, ?> params = [:]) {
        // Gets the branch path
        String branchPath = params['branchPath'] as String
        if (!branchPath) throw new IllegalStateException("Missing `branchPath` parameter.")
        // Gets the build link
        def buildRevisionLink = [:]
        if (params.containsKey('link')) {
            def linkId = params['link']
            def linkData = params['data']
            buildRevisionLink = [
                    id: linkId,
                    data: linkData,
            ]
        }
        // Setting the property
        property('net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType', [
                branchPath: branchPath,
                buildRevisionLink : buildRevisionLink,
        ])
    }

    def getSvn() {
        property('net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType')
    }

    /**
     * SVN revision change log issue validator
     */
    def svnValidatorClosedIssues(Collection<String> closedStatuses) {
        property('net.nemerosa.ontrack.extension.svn.property.SVNRevisionChangeLogIssueValidator', [
                closedStatuses: closedStatuses
        ])
    }

    def getSvnValidatorClosedIssues() {
        property('net.nemerosa.ontrack.extension.svn.property.SVNRevisionChangeLogIssueValidator')
    }

    /**
     * SVN synchronisation
     */

    def svnSync(int interval = 0, boolean override = false) {
        property('net.nemerosa.ontrack.extension.svn.property.SVNSyncPropertyType', [
                override: override,
                interval: interval,
        ])
    }

    def getSvnSync() {
        property('net.nemerosa.ontrack.extension.svn.property.SVNSyncPropertyType')
    }

    /**
     * Artifactory synchronisation
     */

    def artifactorySync(String configuration, String buildName, String buildNameFilter = '*', int interval = 0) {
        property('net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncPropertyType', [
                configuration: configuration,
                buildName: buildName,
                buildNameFilter: buildNameFilter,
                interval: interval
        ])
    }

    def getArtifactorySync() {
        property('net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncPropertyType')
    }

}
