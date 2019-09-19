package net.nemerosa.ontrack.extension.svn.property

import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration
import net.nemerosa.ontrack.model.support.ConfigurationProperty

/**
 * Associates a [net.nemerosa.ontrack.model.structure.Project] with a
 * [net.nemerosa.ontrack.extension.svn.model.SVNConfiguration].
 *
 * @property configuration Link to the SVN configuration
 * @property projectPath Path of the main project branch (trunk) in this configuration. The path is relative to the root
 * of the repository.
 */
class SVNProjectConfigurationProperty(
        private val configuration: SVNConfiguration,
        val projectPath: String
) : ConfigurationProperty<SVNConfiguration> {

    override fun getConfiguration(): SVNConfiguration = configuration

    /**
     * Derived property: the full URL to the Subversion URL.
     */
    val url: String
        get() = configuration.getUrl(projectPath)

}
