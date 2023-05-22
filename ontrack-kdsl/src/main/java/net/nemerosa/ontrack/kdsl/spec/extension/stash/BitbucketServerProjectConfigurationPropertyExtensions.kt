package net.nemerosa.ontrack.kdsl.spec.extension.stash

import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.kdsl.spec.Project
import net.nemerosa.ontrack.kdsl.spec.deleteProperty
import net.nemerosa.ontrack.kdsl.spec.getProperty
import net.nemerosa.ontrack.kdsl.spec.setProperty

/**
 * Sets a Bitbucket Server property on a project.
 */
var Project.bitbucketServerConfigurationProperty: BitbucketServerProjectConfigurationProperty?
    get() = getProperty(BITBUCKET_SERVER_PROJECT_CONFIGURATION_PROPERTY)?.parse()
    set(value) {
        if (value != null) {
            setProperty(BITBUCKET_SERVER_PROJECT_CONFIGURATION_PROPERTY, value)
        } else {
            deleteProperty(BITBUCKET_SERVER_PROJECT_CONFIGURATION_PROPERTY)
        }
    }


class BitbucketServerProjectConfigurationProperty(
        val configuration: String,
        val project: String,
        val repository: String,
        val indexationInterval: Int = 0,
        val issueServiceConfigurationIdentifier: String? = null,
)

const val BITBUCKET_SERVER_PROJECT_CONFIGURATION_PROPERTY =
        "net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType"