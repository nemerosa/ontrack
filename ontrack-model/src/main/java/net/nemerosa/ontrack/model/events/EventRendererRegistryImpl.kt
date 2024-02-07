package net.nemerosa.ontrack.model.events

import org.springframework.stereotype.Service

@Service
class EventRendererRegistryImpl(
    override val eventRenderers: List<EventRenderer>,
) : EventRendererRegistry
