package net.nemerosa.ontrack.model.dashboards.widgets

interface WidgetService {

    /**
     * Gets the list of all available widgets
     */
    fun findAll(): List<Widget<*>>

}