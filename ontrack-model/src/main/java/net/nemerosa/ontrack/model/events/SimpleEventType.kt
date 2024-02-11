package net.nemerosa.ontrack.model.events

data class SimpleEventType(
    override val id: String,
    override val template: String,
    override val description: String,
) : EventType {

    companion object {
        @JvmStatic
        fun of(id: String, template: String, description: String): EventType {
            return SimpleEventType(id, template, description)
        }
    }

}
