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

    /**
     * Compatibility with version 2.15 and older
     */
    @Deprecated
    def svn(String branchPath, String buildPath) {
        // Build path expression
        String buildPlaceholderPattern = "\\{(.+)\\}"
        // Link data
        String linkId
        Map linkData = [:]
        // Detecting the link type
        // Revision
        if (buildPath.endsWith('@{build}')) {
            linkId = 'revision'
        }
        // Tag or pattern
        else {
            Pattern pattern = Pattern.compile(buildPlaceholderPattern)
            Matcher matcher = pattern.matcher(buildPath)
            if (matcher.find()) {
                String expression = matcher.group(1);
                if ("build".equals(expression)) {
                    linkId = 'tag'
                } else if (expression.startsWith("build:")) {
                    String buildExpression = expression - "build:";
                    linkId = 'tagPattern'
                    linkData = [
                            pattern: buildExpression,
                    ]
                } else {
                    throw new IllegalStateException("buildPath = ${buildPath} is not supported.")
                }
            } else {
                throw new IllegalStateException("buildPath = ${buildPath} is not supported.")
            }
        }
        // Logging
        LOGGER.warn("[svn] The svn(branchPath=${branchPath}, buildPath=${buildPath}) call is deprecated and will be deleted in future versions")
        // New call
        svn([
                branchPath: branchPath,
                link      : linkId,
                data      : linkData,
        ])
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

    @DSLMethod
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
