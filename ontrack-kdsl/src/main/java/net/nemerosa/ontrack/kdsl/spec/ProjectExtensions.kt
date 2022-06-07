package net.nemerosa.ontrack.kdsl.spec

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.ProjectDeletePropertyMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.ProjectGetPropertyQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.ProjectSetPropertyMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.ProjectFragment
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

/**
 * Creates a [Project] from a GraphQL [ProjectFragment].
 */
fun ProjectFragment.toProject(connected: Connected) = Project(
    connector = connected.connector,
    id = id().toUInt(),
    name = name()!!,
    description = description(),
)

/**
 * Gets a generic property on a [Project].
 *
 * @param type FQCN of the property type
 * @return Property raw data as JSON
 */
fun Project.getProperty(
    type: String,
): JsonNode? =
    graphqlConnector.query(
        ProjectGetPropertyQuery(id.toInt(), type)
    )?.projects()?.firstOrNull()?.properties()?.firstOrNull()?.value()?.asJson()

/**
 * Sets a generic property on a [Project].
 *
 * @param type FQCN of the property type
 * @param data Property raw data (will be converted into JSON)
 */
fun Project.setProperty(
    type: String,
    data: Any,
): Project {
    graphqlConnector.mutate(
        ProjectSetPropertyMutation(
            id.toInt(),
            type,
            data.asJson()
        )
    ) {
        it?.setProjectPropertyById()?.fragments()?.payloadUserErrors()?.convert()
    }
    return this
}

/**
 * Deletes a generic property on a [Project].
 *
 * @param type FQCN of the property type
 */
fun Project.deleteProperty(
    type: String,
): Project {
    graphqlConnector.mutate(
        ProjectDeletePropertyMutation(
            id.toInt(),
            type
        )
    ) {
        it?.setProjectPropertyById()?.fragments()?.payloadUserErrors()?.convert()
    }
    return this
}