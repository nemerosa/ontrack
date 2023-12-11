package net.nemerosa.ontrack.model.dashboards.widgets

abstract class AbstractWidget<C : WidgetConfig>(
    override val key: String,
    override val name: String,
    override val description: String,
    override val defaultConfig: C,
    override val preferredHeight: Int,
) : Widget<C>
