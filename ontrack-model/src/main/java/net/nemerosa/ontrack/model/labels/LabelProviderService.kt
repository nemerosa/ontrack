package net.nemerosa.ontrack.model.labels

import net.nemerosa.ontrack.model.structure.Project

interface LabelProviderService {

    /**
     * Gets a [LabelProvider] given its ID.
     *
     * @param id ID of the label provider
     * @return Label provider or `null` if not found
     */
    fun getLabelProvider(id: String): LabelProvider?

    /**
     * Collects and saves all labels for this project.
     */
    fun collectLabels(project: Project)
}