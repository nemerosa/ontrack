package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.model.IndicatorType

class ProjectIndicatorType(
        val id: Int,
        val name: String,
        val link: String?
) {
    constructor(type: IndicatorType<out Any?, out Any?>) : this(
            id = type.id,
            name = type.longName,
            link = type.link
    )
}