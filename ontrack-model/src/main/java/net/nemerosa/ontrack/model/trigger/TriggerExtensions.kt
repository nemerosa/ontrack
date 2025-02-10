package net.nemerosa.ontrack.model.trigger

import net.nemerosa.ontrack.json.asJson

fun <T> Trigger<T>.createTriggerData(data: T) = TriggerData(
    id = id,
    data = data.asJson(),
)
