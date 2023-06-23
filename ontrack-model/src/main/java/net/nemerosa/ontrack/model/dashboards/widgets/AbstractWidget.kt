package net.nemerosa.ontrack.model.dashboards.widgets

abstract class AbstractWidget<C: WidgetConfig>(
    override val key: String,
    override val name: String,
) : Widget<C>
