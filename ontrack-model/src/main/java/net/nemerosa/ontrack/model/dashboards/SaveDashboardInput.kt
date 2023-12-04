package net.nemerosa.ontrack.model.dashboards

data class SaveDashboardInput(
    val uuid: String?,
    val name: String,
    val userScope: DashboardContextUserScope,
    val widgets: List<WidgetInstanceInput>,
    val select: Boolean,
)
