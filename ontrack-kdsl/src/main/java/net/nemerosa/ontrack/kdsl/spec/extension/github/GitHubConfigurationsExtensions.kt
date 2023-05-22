package net.nemerosa.ontrack.kdsl.spec.extension.github

import net.nemerosa.ontrack.kdsl.spec.configurations.ConfigurationInterface
import net.nemerosa.ontrack.kdsl.spec.configurations.ConfigurationsMgt

val ConfigurationsMgt.gitHub: ConfigurationInterface<GitHubConfiguration>
    get() = ConfigurationInterface(
            connector = connector,
            id = "github",
            type = GitHubConfiguration::class,
    )
