package net.nemerosa.ontrack.extension.git.mocking

import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.model.json.schema.JsonSchemaString

class GitMockingConfigurationProperty(
    @JsonSchemaString
    val configuration: BasicGitConfiguration,
    val issueServiceConfigurationIdentifier: String?
)