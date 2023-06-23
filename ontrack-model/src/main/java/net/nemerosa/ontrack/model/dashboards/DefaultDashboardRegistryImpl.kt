package net.nemerosa.ontrack.model.dashboards

import org.springframework.stereotype.Service

@Service
class DefaultDashboardRegistryImpl(
    defaultDashboardRegistrations: List<DefaultDashboardRegistration>,
) : DefaultDashboardRegistry {

    private val index: Map<String, Dashboard>

    init {
        val workingIndex = mutableMapOf<String, Dashboard>()
        defaultDashboardRegistrations.forEach { defaultDashboardRegistration ->
            defaultDashboardRegistration.registrations.forEach { (key, dashboard) ->
                if (workingIndex.containsKey(key)) {
                    error("Default dashboard key is registered twice: $key")
                } else {
                    workingIndex[key] = dashboard
                }
            }
        }
        index = workingIndex.toMap()
    }

    override fun findDashboard(key: String): Dashboard? = index[key]

}