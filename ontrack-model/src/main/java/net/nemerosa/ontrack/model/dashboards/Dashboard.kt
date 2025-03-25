package net.nemerosa.ontrack.model.dashboards

/**
 * A _dashboard_ is a _layout_ associated to a list of widgets.
 *
 * @property uuid ID of the dashboard
 * @property name Display name for the dashboard
 * @property userScope Private or shared dashboard
 * @property layoutKey ID of the layout to use (no configuration needed)
 * @property widgets List of widgets in this dashboard
 */
data class Dashboard(
    val uuid: String,
    val name: String,
    val userScope: DashboardContextUserScope,
    val widgets: List<WidgetInstance>,
) {
    fun share() = Dashboard(
        uuid = uuid,
        name = name,
        userScope = DashboardContextUserScope.SHARED,
        widgets = widgets
    )

    /**
     * Migrating all the widgets
     */
    fun migrateWidgets(migration: (WidgetInstance) -> WidgetInstance) =
        Dashboard(
            uuid = uuid,
            name = name,
            userScope = userScope,
            widgets = widgets.map { migration(it) },
        )
}
