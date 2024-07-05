package net.nemerosa.ontrack.kdsl.spec.extension.jira

import net.nemerosa.ontrack.kdsl.spec.configurations.ConfigurationInterface
import net.nemerosa.ontrack.kdsl.spec.configurations.ConfigurationsMgt

val ConfigurationsMgt.jira: ConfigurationInterface<JiraConfiguration>
    get() = ConfigurationInterface(
        connector = connector,
        id = "jira",
        type = JiraConfiguration::class,
    )
