package net.nemerosa.ontrack.extension.git.mocking

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration

class GitMockingConfigurationProperty(
        val configuration: BasicGitConfiguration,
        val issueServiceConfigurationIdentifier: String?
)