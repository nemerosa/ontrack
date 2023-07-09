package net.nemerosa.ontrack.model.dashboards.widgets

import org.springframework.stereotype.Service

@Service
class WidgetServiceImpl(
    private val widgets: List<Widget<*>>,
) : WidgetService {

    override fun findAll(): List<Widget<*>> = widgets.sortedBy { it.name }

}