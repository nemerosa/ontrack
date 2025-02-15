package net.nemerosa.ontrack.model.trigger

fun <T> TriggerRegistry.getTriggerById(id: String): Trigger<T> =
    findTriggerById(id)
        ?: throw TriggerNotFoundException(id)
