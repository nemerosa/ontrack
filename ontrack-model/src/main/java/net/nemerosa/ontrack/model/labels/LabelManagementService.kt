package net.nemerosa.ontrack.model.labels

import net.nemerosa.ontrack.model.Ack

interface LabelManagementService {

    /**
     * Gets the list of all labels, including the ones which are computed
     */
    val labels: List<Label>

    /**
     * Creation of a new label.
     */
    fun newLabel(form: LabelForm): Label

    /**
     * Gets a label using its ID, returning `null` if not found
     */
    fun findLabelById(labelId: Int): Label?

    /**
     * Gets a label using its ID
     *
     * @throws LabelIdNotFoundException If ID cannot be found
     */
    fun getLabel(labelId: Int): Label

    /**
     * Updates an existing label
     */
    fun updateLabel(labelId: Int, form: LabelForm): Label

    /**
     * Deletes a label
     */
    fun deleteLabel(labelId: Int): Ack

    /**
     * Finds a list of labels using their category and/or name.
     */
    fun findLabels(category: String?, name: String?): List<Label>
}
