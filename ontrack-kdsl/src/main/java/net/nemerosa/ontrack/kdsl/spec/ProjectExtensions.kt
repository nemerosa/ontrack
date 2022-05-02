package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.ProjectFragment

/**
 * Creates a [Project] from a GraphQL [ProjectFragment].
 */
fun ProjectFragment.toProject(connected: Connected) = Project(
    connector = connected.connector,
    id = id().toUInt(),
    name = name()!!,
    description = description(),
)