package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.extension.indicators.model.IndicatorSource
import net.nemerosa.ontrack.extension.indicators.model.IndicatorType
import net.nemerosa.ontrack.model.annotations.APIDescription

class ProjectIndicatorType(
        val id: String,
        val name: String,
        val link: String?,
        val category: IndicatorCategory,
        val source: IndicatorSource?,
        @APIDescription("Flag which indicates if the associated project indicators are computed or not")
        val computed: Boolean
) {
    constructor(type: IndicatorType<out Any?, out Any?>) : this(
            id = type.id,
            name = type.name,
            link = type.link,
            category = type.category,
            source = type.source,
            computed = type.computed
    )
}