package net.nemerosa.ontrack.model.events

/**
 * Mock which can be used for unit tests.
 */
class MockEventTemplatingService : EventTemplatingService {

    override fun render(template: String, event: Event, context: Map<String, Any>, renderer: EventRenderer): String {
        TODO("Not yet implemented")
    }

    override fun renderEvent(event: Event, context: Map<String, Any>, template: String?, renderer: EventRenderer): String =
        if (template.isNullOrBlank()) {
            event.eventType.template // Not rendering the template
        } else {
            template // Not rendering the template
        }

}