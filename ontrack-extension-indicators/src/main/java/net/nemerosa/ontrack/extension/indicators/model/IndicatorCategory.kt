package net.nemerosa.ontrack.extension.indicators.model

/**
 * Catagory grouping several [indicator types][IndicatorType] together.
 *
 * @param id Unique ID of the category
 * @param name Display name for the category
 * @param source Optional source for this category. `null` means that the category was provisioned manually.
 * @param deprecated Optional deprecation reason
 */
class IndicatorCategory(
        val id: String,
        val name: String,
        val source: IndicatorSource?,
        val deprecated: String? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IndicatorCategory) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}