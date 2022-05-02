package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.BranchFragment

/**
 * Creates a [Branch] from a GraphQL [BranchFragment].
 */
fun BranchFragment.toBranch(connected: Connected) = Branch(
    connector = connected.connector,
    id = id().toUInt(),
    name = name()!!,
    description = description(),
    disabled = disabled(),
)
