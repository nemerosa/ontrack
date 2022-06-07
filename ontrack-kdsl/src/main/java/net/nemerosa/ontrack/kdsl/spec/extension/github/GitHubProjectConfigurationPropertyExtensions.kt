package net.nemerosa.ontrack.kdsl.spec.extension.github

import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.kdsl.spec.Project
import net.nemerosa.ontrack.kdsl.spec.deleteProperty
import net.nemerosa.ontrack.kdsl.spec.getProperty
import net.nemerosa.ontrack.kdsl.spec.setProperty

/**
 * Sets a GitHub property on a project.
 */
var Project.gitHubConfigurationProperty: GitHubProjectConfigurationProperty?
    get() = getProperty(GITHUB_PROJECT_CONFIGURATION_PROPERTY)?.parse()
    set(value) {
        if (value != null) {
            setProperty(GITHUB_PROJECT_CONFIGURATION_PROPERTY, value)
        } else {
            deleteProperty(GITHUB_PROJECT_CONFIGURATION_PROPERTY)
        }
    }


class GitHubProjectConfigurationProperty(
    val configuration: String,
    val repository: String,
    val indexationInterval: Int = 0,
    val issueServiceConfigurationIdentifier: String? = null,
)

const val GITHUB_PROJECT_CONFIGURATION_PROPERTY =
    "net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType"