package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.model.support.ConfigurationProperty

/**
 * Property linked to a Jenkins configuration.
 *
 * @property configuration Reference to the Jenkins configuration.
 */
abstract class AbstractJenkinsProperty(
    override val configuration: JenkinsConfiguration
) : ConfigurationProperty<JenkinsConfiguration>
