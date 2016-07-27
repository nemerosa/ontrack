package net.nemerosa.ontrack.dsl.properties

import net.nemerosa.ontrack.dsl.Branch
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod
import net.nemerosa.ontrack.dsl.doc.DSLProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.Matcher
import java.util.regex.Pattern

@DSL
@DSLProperties
class BranchProperties extends ProjectEntityProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(BranchProperties)

    BranchProperties(Ontrack ontrack, Branch branch) {
        super(ontrack, branch)
    }

    /**
     * Git branch property
     */
    @DSLMethod(count = 2)
    def gitBranch(String branch, Map<String, ?> params = [:]) {
        property(
                'net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType',
                [branch: branch] + params
        )
    }

    @DSLMethod(see = "gitBranch")
    def getGitBranch() {
        property('net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType')
    }

    /**
     * SVN branch property
     */

    @DSLMethod(count = 1)
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
                    id  : linkId,
                    data: linkData,
            ]
        }
        // Setting the property
        property('net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType', [
                branchPath       : branchPath,
                buildRevisionLink: buildRevisionLink,
        ])
    }

    @DSLMethod(see = "svn")
    def getSvn() {
        property('net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType')
    }

    /**
     * SVN revision change log issue validator
     */

    @DSLMethod
    def svnValidatorClosedIssues(Collection<String> closedStatuses) {
        property('net.nemerosa.ontrack.extension.svn.property.SVNRevisionChangeLogIssueValidator', [
                closedStatuses: closedStatuses
        ])
    }

    @DSLMethod(see = "svnValidatorClosedIssues")
    def getSvnValidatorClosedIssues() {
        property('net.nemerosa.ontrack.extension.svn.property.SVNRevisionChangeLogIssueValidator')
    }

    /**
     * SVN synchronisation
     */

    @DSLMethod(count = 2)
    def svnSync(int interval = 0, boolean override = false) {
        property('net.nemerosa.ontrack.extension.svn.property.SVNSyncPropertyType', [
                override: override,
                interval: interval,
        ])
    }

    @DSLMethod(see = "svnSync")
    def getSvnSync() {
        property('net.nemerosa.ontrack.extension.svn.property.SVNSyncPropertyType')
    }

    /**
     * Artifactory synchronisation
     */

    @DSLMethod(count = 4)
    def artifactorySync(String configuration, String buildName, String buildNameFilter = '*', int interval = 0) {
        property('net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncPropertyType', [
                configuration  : configuration,
                buildName      : buildName,
                buildNameFilter: buildNameFilter,
                interval       : interval
        ])
    }

    @DSLMethod(see = "artifactorySync")
    def getArtifactorySync() {
        property('net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncPropertyType')
    }

}
