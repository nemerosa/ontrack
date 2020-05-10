package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.extension.indicators.model.IndicatorType

class ProjectIndicatorType(
        val id: String,
        val shortName: String,
        val name: String,
        val link: String?,
        val category: IndicatorCategory
) {
    constructor(type: IndicatorType<out Any?, out Any?>) : this(
            id = type.id,
            shortName = type.shortName,
            name = type.longName,
            link = type.link,
            category = type.category
    )
}