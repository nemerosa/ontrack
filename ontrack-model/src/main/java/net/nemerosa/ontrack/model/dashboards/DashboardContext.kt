package net.nemerosa.ontrack.model.dashboards

data class DashboardContext(
    val key: String,
    val id: String,
) {
    fun contextKey(
        userId: String,
        userScope: DashboardContextUserScope,
        contextScope: DashboardContextScope,
    ) = when (userScope) {

        DashboardContextUserScope.USER -> when (contextScope) {
            DashboardContextScope.ID -> "user:${userId}:id:${key}:${id}"
            DashboardContextScope.CONTEXT -> "user:${userId}:key:${key}"
        }

        DashboardContextUserScope.GLOBAL -> when (contextScope) {
            DashboardContextScope.ID -> "global:id:${key}:${id}"
            DashboardContextScope.CONTEXT -> "global:key:${key}"
        }
    }
}
