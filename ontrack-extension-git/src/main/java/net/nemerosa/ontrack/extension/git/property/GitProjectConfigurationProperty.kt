package net.nemerosa.ontrack.extension.git.property

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.model.docs.DocumentationType
import net.nemerosa.ontrack.model.support.ConfigurationProperty

class GitProjectConfigurationProperty(
    @APIDescription("Link to the Git configuration")
    @DocumentationType("String", "Name of the Git configuration")
    override val configuration: BasicGitConfiguration,
) : ConfigurationProperty<BasicGitConfiguration>
