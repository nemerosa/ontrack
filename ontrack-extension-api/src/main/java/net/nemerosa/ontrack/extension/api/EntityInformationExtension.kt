package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.extension.api.model.EntityInformation
import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.ProjectEntity

/**
 * Defines an extension that can provide additional content to an
 * [entity][net.nemerosa.ontrack.model.structure.ProjectEntity].
 */
interface EntityInformationExtension : Extension {
    /**
     * Gets information for an entity. Returns `null` when none is available.
     */
    fun getInformation(entity: ProjectEntity): EntityInformation?
}