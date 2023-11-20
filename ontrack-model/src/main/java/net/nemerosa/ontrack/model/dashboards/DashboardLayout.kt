package net.nemerosa.ontrack.model.dashboards

/**
 * A _dashboard layout_ allows the _widgets_ of a _dashboard_ to be displayed in a given disposition.
 *
 * @property key The ID of the layout
 * @property name Display name for the layout
 * @property description Description for the layout
 */
data class DashboardLayout(
    val key: String,
    val name: String,
    val description: String,
)