package net.nemerosa.ontrack.model.events

/**
 * Facade for the templating of events.
 */
interface EventTemplatingService {

    /**
     * Renders a template for an event.
     *
     * @param template Content of the template
     * @param event Event context
     * @param renderer Target renderer
     * @return Rendered template
     */
    fun render(
        template: String,
        event: Event,
        renderer: EventRenderer,
    ): String

}