package net.nemerosa.ontrack.model.events

fun Event.dehydrate() = SerializableEvent(
    eventType = eventType.id,
    signature = signature,
    entities = entities.mapValues { (_, entity) -> entity.id() },
    extraEntities = extraEntities.mapValues { (_, entity) -> entity.id() },
    ref = ref,
    values = values,
)