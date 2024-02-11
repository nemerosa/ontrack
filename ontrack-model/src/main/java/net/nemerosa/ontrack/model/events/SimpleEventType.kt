package net.nemerosa.ontrack.model.events

data class SimpleEventType(
    override val id: String,
    override val template: String,
    override val description: String,
    override val context: EventTypeContext,
) : EventType
