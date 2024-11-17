package net.nemerosa.ontrack.kdsl.spec.extension.environments

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.EnvironmentFragment

fun EnvironmentFragment.toEnvironment(connected: Connected) = Environment(
    connector = connected.connector,
    id = id(),
    name = name(),
    order = order()!!,
    description = description(),
    tags = tags(),
)
