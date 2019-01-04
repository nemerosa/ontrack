package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.labels.LabelForm

interface LabelRepository {

    /**
     * Creation of a new label
     */
    fun newLabel(form: LabelForm): LabelRecord

    /**
     * Creates an automated label, overriding any manual one
     */
    fun overrideLabel(form: LabelForm, providerId: String): LabelRecord

    /**
     * Gets a label by its ID
     */
    fun getLabel(labelId: Int): LabelRecord

    /**
     * Updates a label
     */
    fun updateLabel(labelId: Int, form: LabelForm): LabelRecord

    /**
     * Updates a automated label
     */
    fun updateAndOverrideLabel(labelId: Int, form: LabelForm, providerId: String): LabelRecord

    /**
     * Deletes a label
     */
    fun deleteLabel(labelId: Int): Ack

    /**
     * Gets the list of existing label for the given provider id ("computed by")
     */
    fun findLabelsByProvider(providerId: String): List<LabelRecord>

    /**
     * Finds a single record for the given attributes.
     */
    fun findLabelByCategoryAndNameAndProvider(category: String?, name: String, providerId: String): LabelRecord?

    /**
     * Gets list of all labels, ordered by category and name
     */
    val labels: List<LabelRecord>
}

class LabelRecord(
        val id: Int,
        val category: String?,
        val name: String,
        val description: String?,
        val color: String,
        val computedBy: String?
) {
    /**
     * Conversion to a form
     */
    fun toLabelForm() = LabelForm(
            category = category,
            name = name,
            description = description,
            color = color
    )

    /**
     * Comparing the [category] and [name].
     */
    fun sameThan(form: LabelForm): Boolean {
        return category == form.category && name == form.name
    }
}
