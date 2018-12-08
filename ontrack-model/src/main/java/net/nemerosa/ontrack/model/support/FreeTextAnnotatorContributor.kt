package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.model.structure.ProjectEntity

/**
 * Define a [MessageAnnotator] to be used for free text.
 */
interface FreeTextAnnotatorContributor {
    /**
     * Gets the [MessageAnnotator] to be used.
     */
    fun getMessageAnnotator(entity:ProjectEntity): MessageAnnotator?
}