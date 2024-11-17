package net.nemerosa.ontrack.kdsl.spec.extension.environments

import net.nemerosa.ontrack.kdsl.spec.Ontrack

/**
 * Management of environments, slots & pipelines in Ontrack.
 */
val Ontrack.environments: EnvironmentsMgt get() = EnvironmentsMgt(connector)
