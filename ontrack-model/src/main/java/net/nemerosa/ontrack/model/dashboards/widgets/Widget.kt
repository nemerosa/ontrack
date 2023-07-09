package net.nemerosa.ontrack.model.dashboards.widgets

interface Widget<C: WidgetConfig> {

    val key: String
    val name: String
    val description: String
    val defaultConfig: C

}