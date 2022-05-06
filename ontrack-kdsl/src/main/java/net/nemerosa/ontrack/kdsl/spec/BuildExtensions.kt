package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.BuildSetPropertyMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.BuildFragment
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

/**
 * Creates a [Build] from a GraphQL [BuildFragment].
 */
fun BuildFragment.toBuild(connected: Connected) = Build(
    connector = connected.connector,
    id = id().toUInt(),
    name = name()!!,
    description = description(),
)

/**
 * Sets a generic property on a [Build].
 *
 * @param type FQCN of the property type
 * @param data Property raw data (will be converted into JSON)
 */
fun Build.setProperty(
    type: String,
    data: Any,
): Build {
    graphqlConnector.mutate(
        BuildSetPropertyMutation(
            id.toInt(),
            type,
            data.asJson()
        )
    ) {
        it?.setBuildPropertyById()?.fragments()?.payloadUserErrors()?.convert()
    }
    return this
}