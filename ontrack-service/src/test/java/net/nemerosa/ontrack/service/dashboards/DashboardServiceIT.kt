package net.nemerosa.ontrack.service.dashboards

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.dashboards.*
import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class DashboardServiceIT: AbstractDSLTestSupport() {

    @Autowired
    private lateinit var dashboardService: DashboardService

    @Test
    fun `Deleting a shared dashboard makes it unavailable as the default dashboard for all users`() {
        val participant = doCreateAccountWithGlobalRole(Roles.GLOBAL_PARTICIPANT)
        val admin = doCreateAccountWithGlobalRole(Roles.GLOBAL_ADMINISTRATOR)

        // Admin creating & sharing a dashboard
        val dashboard = asFixedAccount(admin) {
               dashboardService.saveDashboard(
                   SaveDashboardInput(
                       uuid = null,
                       name = uid("dsh_"),
                       userScope = DashboardContextUserScope.SHARED,
                       layoutKey = DashboardLayouts.defaultLayout.key,
                       widgets = emptyList(),
                       select = false,
                   )
               )
        }

        // User to make this dashboard their default
        asFixedAccount(participant) {
            dashboardService.selectDashboard(dashboard.uuid)
        }

        // Checking the default dashboard for the user
        asFixedAccount(participant) {
            assertEquals(
                dashboard.uuid,
                dashboardService.userDashboard().uuid,
                "Dashboard is selected"
            )
        }

        // Deleting the dashboard
        asFixedAccount(admin) {
            dashboardService.deleteDashboard(dashboard.uuid)
        }

        // Checking we're back on the default dashboard
        asFixedAccount(participant) {
            assertEquals(
                DefaultDashboards.defaultDashboard.uuid,
                dashboardService.userDashboard().uuid,
                "Default dashboard is selected"
            )
        }
    }

}