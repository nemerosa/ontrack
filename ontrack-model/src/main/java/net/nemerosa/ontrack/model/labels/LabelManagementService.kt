package net.nemerosa.ontrack.model.labels

interface LabelManagementService {
    /**
     * Gets the list of all labels, including the ones which are computed
     */
    val labels: List<Label>
}
