package net.nemerosa.ontrack.kdsl.spec

/**
 * Representation of a project.
 *
 * @property id Project ID
 * @property name Project name
 * @property description Project description
 */
class Project(
    val id: UInt,
    val name: String,
    val description: String?,
) {
    /**
     * Deletes this project
     */
    fun delete(): Unit = TODO()

}
