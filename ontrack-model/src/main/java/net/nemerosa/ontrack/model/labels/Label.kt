package net.nemerosa.ontrack.model.labels

import net.nemerosa.ontrack.common.toRGBColor

open class Label(
        val id: Int,
        val category: String?,
        val name: String,
        val description: String?,
        val color: String,
        val computedBy: LabelProviderDescription?
) {
    /**
     * Foreground colour
     */
    val foregroundColor: String get() = color.toRGBColor().toBlackOrWhite().toString()
}
