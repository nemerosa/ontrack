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
