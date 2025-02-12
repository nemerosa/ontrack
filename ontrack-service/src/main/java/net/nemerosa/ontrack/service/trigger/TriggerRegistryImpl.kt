package net.nemerosa.ontrack.service.trigger

import net.nemerosa.ontrack.model.trigger.Trigger
import net.nemerosa.ontrack.model.trigger.TriggerRegistry
import org.springframework.stereotype.Component

@Component
class TriggerRegistryImpl(
    triggers: List<Trigger<*>>,
) : TriggerRegistry {

    private val index = triggers.associateBy { it.id }

    override val triggers: List<Trigger<*>> = triggers.sortedBy { it.displayName }

    @Suppress("UNCHECKED_CAST")
    override fun <T> findTriggerById(id: String): Trigger<T>? = index[id] as Trigger<T>?

}