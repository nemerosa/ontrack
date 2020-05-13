package net.nemerosa.ontrack.extension.indicators.model

/**
 * Catagory grouping several [indicator types][IndicatorType] together.
 *
 * @param id Unique ID of the category
 * @param name Display name for the category
 * @param source Optional source for this category. `null` means that the category was provisioned manually.
 */
class IndicatorCategory(
        val id: String,
        val name: String,
        val source: IndicatorSource?
)
