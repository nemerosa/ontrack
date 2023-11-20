package net.nemerosa.ontrack.model.dashboards

data class DashboardAuthorizations(
    val edit: Boolean,
    val share: Boolean,
    val delete: Boolean,
) {
    companion object {
        val NONE = DashboardAuthorizations(
            edit = false,
            share = false,
            delete = false,
        )
    }
}