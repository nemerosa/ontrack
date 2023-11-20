package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.ProjectEntity

/**
 * This extension allows the enrichment of template parameters values
 * for an [event][Event].
 */
interface EventParameterExtension : Extension {

    /**
     * Gets additional template parameter values for an entity.
     */
    fun additionalTemplateParameters(entity: ProjectEntity): Map<String, String>

}