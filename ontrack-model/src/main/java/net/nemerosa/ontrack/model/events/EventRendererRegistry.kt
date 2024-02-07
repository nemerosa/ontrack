package net.nemerosa.ontrack.model.events

interface EventRendererRegistry {

    fun findEventRendererById(id: String): EventRenderer?

    val eventRenderers: List<EventRenderer>

}