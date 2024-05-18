package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.model.docs.DocumentationType
import net.nemerosa.ontrack.model.support.ConfigurationProperty

/**
 * Property linked to a Jenkins configuration.
 *
 * @property configuration Reference to the Jenkins configuration.
 */
abstract class AbstractJenkinsProperty(
    @DocumentationType("String", "Name of the Jenkins configuration")
    override val configuration: JenkinsConfiguration
) : ConfigurationProperty<JenkinsConfiguration>
