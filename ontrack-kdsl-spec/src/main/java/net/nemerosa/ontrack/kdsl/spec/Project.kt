package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.DeleteProjectByIdMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

/**
 * Representation of a project.
 *
 * @property id Project ID
 * @property name Project name
 * @property description Project description
 */
class Project(
    connector: Connector,
    id: UInt,
    val name: String,
    val description: String?,
) : ProjectEntity(connector, id) {
    /**
     * Deletes this project
     */
    fun delete() {
        graphqlConnector.mutate(
            DeleteProjectByIdMutation(id.toInt())
        ) {
            it?.deleteProject()?.fragments()?.payloadUserErrors()?.convert()
        }
    }

}
