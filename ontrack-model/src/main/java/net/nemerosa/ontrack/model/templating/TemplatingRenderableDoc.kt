package net.nemerosa.ontrack.model.templating

/**
 * This interface is used to create beans which act as documentation
 * for instances of [TemplatingRenderable] object.
 */
interface TemplatingRenderableDoc {

    /**
     * This ID is used to identify the renderable into a context
     */
    val id: String

    /**
     * Display name of the renderable (used for documentation only)
     */
    val displayName: String

    /**
     * Where this renderable can be used
     */
    val contextName: String

    /**
     * Supported fields
     */
    val fields: List<TemplatingRenderableDocField>

}