package net.nemerosa.ontrack.model.events

import org.springframework.stereotype.Service

@Service
class EventRendererRegistryImpl(
    final override val eventRenderers: List<EventRenderer>,
) : EventRendererRegistry {

    private val index = eventRenderers.associateBy { it.id }

    override fun findEventRendererById(id: String): EventRenderer? = index[id]
}
