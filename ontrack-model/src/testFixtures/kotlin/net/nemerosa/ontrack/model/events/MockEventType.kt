package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.support.NameValue

object MockEventType : EventType {

    const val EVENT_MOCK = "mock"

    override val id: String = "mock"
    override val template: String = "Mock event"
    override val description: String = "Mock event"
    override val context = eventContext(
        eventValue(EVENT_MOCK, "Mock test"),
    )

    fun mockEvent(text: String) = Event.of(MockEventType)
        .with(EVENT_MOCK, text)
        .build()

    fun serializedMockEvent(text: String) = SerializableEvent(
        id = 1,
        eventType = id,
        signature = Signature.of("test"),
        entities = emptyMap(),
        extraEntities = emptyMap(),
        ref = null,
        values = mapOf(EVENT_MOCK to NameValue(EVENT_MOCK, text)),
    )
}