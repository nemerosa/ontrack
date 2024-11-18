package net.nemerosa.ontrack.model.events

interface SerializableEventService {

    fun dehydrate(event: Event): SerializableEvent

    fun hydrate(serialized: SerializableEvent): Event

}