package net.nemerosa.ontrack.model.support

/**
 * Define a [MessageAnnotator] to be used for free text.
 */
interface FreeTextAnnotatorContributor {
    /**
     * Gets the [MessageAnnotator] to be used.
     */
    val messageAnnotator: MessageAnnotator
}