package net.nemerosa.ontrack.model.events

/**
 * Mock which can be used for unit tests.
 */
class MockEventTemplatingService : EventTemplatingService {

    override fun render(template: String, event: Event, renderer: EventRenderer): String {
        TODO("Not yet implemented")
    }

    override fun renderEvent(event: Event, template: String?, renderer: EventRenderer): String =
        if (template.isNullOrBlank()) {
            event.renderText()
        } else {
            template // Not rendering the template
        }

}