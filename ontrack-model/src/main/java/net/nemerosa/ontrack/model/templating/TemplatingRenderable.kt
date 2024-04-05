package net.nemerosa.ontrack.model.templating

import net.nemerosa.ontrack.model.events.EventRenderer

/**
 * This interface is used to mark objects in the templating context as being able to
 * deal with their own rendering.
 */
interface TemplatingRenderable {

    /**
     * Renderer this element with the additional parameters.
     *
     * @param field Optional field for this renderable
     * @param configMap Optional configuration for this renderable
     * @param renderer Target renderer
     * @return Rendered template
     */
    fun render(field: String?, configMap: Map<String, String>, renderer: EventRenderer): String
}