package net.nemerosa.ontrack.kdsl.spec.extension.jenkins

import net.nemerosa.ontrack.kdsl.spec.configurations.ConfigurationInterface
import net.nemerosa.ontrack.kdsl.spec.configurations.ConfigurationsMgt

val ConfigurationsMgt.jenkins: ConfigurationInterface<JenkinsConfiguration>
    get() = ConfigurationInterface(
            connector = connector,
            id = "jenkins",
            type = JenkinsConfiguration::class,
    )
