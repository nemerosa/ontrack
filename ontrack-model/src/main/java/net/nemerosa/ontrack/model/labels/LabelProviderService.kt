package net.nemerosa.ontrack.model.labels

interface LabelProviderService {

    /**
     * Gets a [LabelProvider] given its ID.
     *
     * @param id ID of the label provider
     * @return Label provider or `null` if not found
     */
    fun getLabelProvider(id: String): LabelProvider?
}