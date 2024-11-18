package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.support.NameValue

/**
 * Event which can be serialized
 */
data class SerializableEvent(
    /**
     * Type of the event, as a string
     */
    val eventType: String,

    /**
     * Signature of the event
     */
    val signature: Signature?,

    /**
     * List of entities at the source of the event
     */
    val entities: Map<ProjectEntityType, Int>,

    /**
     * List of entities participating into the event.
     *
     * Their semantics depend on the event type.
     */
    val extraEntities: Map<ProjectEntityType, Int>,

    /**
     * Main entity for the entity (depends on the event type)
     */
    val ref: ProjectEntityType?,

    /**
     * Arbitrary name/values linked to the event
     */
    val values: Map<String, NameValue>,
) {

    fun findValue(name: String): String? = values[name]?.value

    fun withValue(name: String, value: String) = SerializableEvent(
        eventType = eventType,
        signature = signature,
        entities = entities,
        extraEntities = extraEntities,
        ref = ref,
        values = values + (name to NameValue(name, value))
    )

    fun findEntityId(projectEntityType: ProjectEntityType): Int? = entities[projectEntityType]

    fun withEntity(entity: ProjectEntity) = SerializableEvent(
        eventType = eventType,
        signature = signature,
        entities = entities + (entity.projectEntityType to entity.id()),
        extraEntities = extraEntities,
        ref = ref,
        values = values
    )
}
