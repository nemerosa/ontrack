package net.nemerosa.ontrack.kdsl.spec.configurations

import net.nemerosa.ontrack.kdsl.spec.Ontrack

val Ontrack.configurations: ConfigurationsMgt get() = ConfigurationsMgt(connector)
