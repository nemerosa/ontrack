package net.nemerosa.ontrack.kdsl.spec.extension.stash

import net.nemerosa.ontrack.kdsl.spec.configurations.ConfigurationInterface
import net.nemerosa.ontrack.kdsl.spec.configurations.ConfigurationsMgt

val ConfigurationsMgt.bitbucketServer: ConfigurationInterface<BitbucketServerConfiguration>
    get() = ConfigurationInterface(
            connector = connector,
            id = "stash",
            type = BitbucketServerConfiguration::class,
    )
