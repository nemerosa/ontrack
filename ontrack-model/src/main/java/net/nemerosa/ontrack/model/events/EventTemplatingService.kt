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

    /**
     * Renders an event with its default message or alternatively another template
     *
     * @param event Event to render
     * @param template Content of the alternative template
     * @param renderer Target renderer
     * @return Rendered template
     */
    fun renderEvent(
        event: Event,
        template: String? = null,
        renderer: EventRenderer = PlainEventRenderer.INSTANCE,
    ): String

}