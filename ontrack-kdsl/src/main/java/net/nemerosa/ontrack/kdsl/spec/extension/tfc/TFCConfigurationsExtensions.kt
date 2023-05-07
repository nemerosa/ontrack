package net.nemerosa.ontrack.kdsl.spec.extension.tfc

import net.nemerosa.ontrack.kdsl.spec.configurations.ConfigurationInterface
import net.nemerosa.ontrack.kdsl.spec.configurations.ConfigurationsMgt

val ConfigurationsMgt.tfc: ConfigurationInterface<TFCConfiguration>
    get() = ConfigurationInterface(
        connector = connector,
        id = "tfc",
        type = TFCConfiguration::class,
    )
