package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.BuildFragment

/**
 * Creates a [Build] from a GraphQL [BuildFragment].
 */
fun BuildFragment.toBuild(connected: Connected) = Build(
    connector = connected.connector,
    id = id().toUInt(),
    name = name()!!,
    description = description(),
)
