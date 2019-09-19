package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.model.structure.ProjectEntity

/**
 * Define a list of [MessageAnnotator]s to be used for free text.
 */
interface FreeTextAnnotatorContributor {
    /**
     * Gets the [MessageAnnotator]s to be used.
     */
    fun getMessageAnnotators(entity: ProjectEntity): List<MessageAnnotator>
}