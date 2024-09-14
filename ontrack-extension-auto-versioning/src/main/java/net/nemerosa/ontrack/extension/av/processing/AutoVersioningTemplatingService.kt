package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder

/**
 * Facade to the templating, tuned for the needs of the auto-versioning processing.
 */
interface AutoVersioningTemplatingService {

    /**
     * Returns a templating renderer for a given auto-versioning context.
     *
     * @param order Auto-versioning order being processed
     * @param currentVersions Map of current versions per target path
     * @return A renderer for the auto-versioning context
     */
    fun createAutoVersioningTemplateRenderer(
        order: AutoVersioningOrder,
        currentVersions: Map<String, String>,
    ): AutoVersioningTemplateRenderer

    /**
     * Generates the information needed for the creation of an auto-versioning PR.
     *
     * @param order Auto-versioning order being processed
     * @param avRenderer Renderer used to generate templates for this AV context
     * @return PR info
     */
    fun generatePRInfo(
        order: AutoVersioningOrder,
        avRenderer: AutoVersioningTemplateRenderer,
    ): AutoVersioningPRInfo

}