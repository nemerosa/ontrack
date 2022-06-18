package net.nemerosa.ontrack.kdsl.spec.extension.git

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.kdsl.spec.Branch
import net.nemerosa.ontrack.kdsl.spec.deleteProperty
import net.nemerosa.ontrack.kdsl.spec.getProperty
import net.nemerosa.ontrack.kdsl.spec.setProperty
import net.nemerosa.ontrack.kdsl.spec.support.ServiceConfiguration

const val GIT_BRANCH_CONFIGURATION_PROPERTY =
    "net.nemerosa.ontrack.extension.git.property.GitBranchConfigurationPropertyType"

var Branch.gitBranchConfigurationProperty: GitBranchConfigurationProperty?
    get() = getProperty(GIT_BRANCH_CONFIGURATION_PROPERTY)?.parse()
    set(value) {
        if (value != null) {
            setProperty(GIT_BRANCH_CONFIGURATION_PROPERTY, value)
        } else {
            deleteProperty(GIT_BRANCH_CONFIGURATION_PROPERTY)
        }
    }

var Branch.gitBranchConfigurationPropertyBranch: String?
    get() = gitBranchConfigurationProperty?.branch
    set(value) {
        gitBranchConfigurationProperty = if (value != null) {
            GitBranchConfigurationProperty(branch = value)
        } else {
            null
        }
    }

@JsonIgnoreProperties(ignoreUnknown = true)
class GitBranchConfigurationProperty(

    /**
     * Git branch or pull request ID
     */
    val branch: String,

    /**
     * Build link
     */
    val buildCommitLink: ServiceConfiguration? = ServiceConfiguration(
        id = "git-commit-property",
        data = null,
    ),

    /**
     * Build overriding policy when synchronizing
     */
    val isOverride: Boolean = false,

    /**
     * Interval in minutes for build/tag synchronization
     */
    val buildTagInterval: Int = 0,

    )
