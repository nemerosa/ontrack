package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.model.structure.ID.Companion.isDefined

/**
 * A `ProjectEntity` is an [Entity] that belongs into a [Project]. It has also a [description].
 */
interface ProjectEntity : Entity {

    /**
     * Gets the description of this entity.
     */
    val description: String?

    /**
     * Returns the project this entity is associated with
     */
    @get:JsonIgnore
    val project: Project

    /**
     * Returns the ID of the project that contains this entity. This method won't return `null`
     * but the ID could be [undefined][ID.NONE].
     */
    @get:JsonIgnore
    val projectId: ID
        get() = project.id

    /**
     * Shortcut to get the ID as a value.
     *
     * @throws IllegalArgumentException If the project ID is not [set][ID.isSet].
     */
    fun projectId(): Int {
        val id = projectId
        check(isDefined(id)) { "Project ID must be defined" }
        return id.get()
    }

    /**
     * Gets the type of entity as an enum.
     */
    @get:JsonIgnore
    val projectEntityType: ProjectEntityType

    /**
     * Representation, like "Branch P/X"
     */
    @get:JsonIgnore
    val entityDisplayName: String

    /**
     * Creation signature of the project entity.
     *
     * @return Creation signature for the project entity.
     */
    val signature: Signature

}