package net.nemerosa.ontrack.kdsl.spec

import net.nemerosa.ontrack.kdsl.connector.Connector

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
    fun delete(): Unit = TODO()

}
