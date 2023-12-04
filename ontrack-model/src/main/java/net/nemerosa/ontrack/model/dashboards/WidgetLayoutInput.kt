package net.nemerosa.ontrack.model.dashboards

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WidgetLayoutInput(
    val x: Int,
    val y: Int,
    val w: Int,
    val h: Int,
)
