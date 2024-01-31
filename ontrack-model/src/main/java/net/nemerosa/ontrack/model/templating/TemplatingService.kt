package net.nemerosa.ontrack.model.templating

import net.nemerosa.ontrack.model.events.EventRenderer

/**
 * This service is used to render a template for a given
 * context and for a given renderer.
 */
interface TemplatingService {

    /**
     * Checks if a given template contains legacy templating
     * and no new syntax.
     */
    @Deprecated("Legacy templates will be removed in V5.")
    fun isLegacyTemplate(template: String): Boolean

    /**
     * Renders a template.
     *
     * @param template Content of the template
     * @param context Context for the rendering
     * @param renderer Target renderer
     * @return Rendered template
     */
    fun render(
        template: String,
        context: Map<String, Any>,
        renderer: EventRenderer,
    ): String

}