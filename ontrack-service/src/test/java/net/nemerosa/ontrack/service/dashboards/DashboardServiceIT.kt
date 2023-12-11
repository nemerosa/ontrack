package net.nemerosa.ontrack.service.dashboards

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.dashboards.*
import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class DashboardServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var dashboardService: DashboardService

    @Test
    fun `Default dashboard`() {
        withNoDashboard {
            asUser {
                val dashboards = dashboardService.userDashboards()
                assertEquals(1, dashboards.size)
                val dashboard = dashboards.first()
                assertEquals("0", dashboard.uuid)
                assertEquals("Default dashboard", dashboard.name)
                assertEquals(DashboardContextUserScope.BUILT_IN, dashboard.userScope)
                assertEquals(
                    listOf(
                        WidgetInstance(
                            uuid = "0",
                            key = "home/LastActiveProjects",
                            config = mapOf("count" to 10).asJson(),
                            layout = WidgetLayout(x = 0, y = 0, w = 12, h = 4),
                        )
                    ),
                    dashboard.widgets
                )
            }
        }
    }

    @Test
    fun `Default dashboard is the default dashboard`() {
        withNoDashboard {
            asUser {
                val dashboard = dashboardService.userDashboard()
                assertEquals("0", dashboard.uuid)
                assertEquals("Default dashboard", dashboard.name)
                assertEquals(DashboardContextUserScope.BUILT_IN, dashboard.userScope)
                assertEquals(
                    listOf(
                        WidgetInstance(
                            uuid = "0",
                            key = "home/LastActiveProjects",
                            config = mapOf("count" to 10).asJson(),
                            layout = WidgetLayout(x = 0, y = 0, w = 12, h = 4),
                        )
                    ),
                    dashboard.widgets
                )
            }
        }
    }

    @Test
    fun `Creating a personal dashboard makes it the default dashboard`() {
        val name = uid("dash_")
        withNoDashboard {
            asUser().with(DashboardEdition::class.java).call {
                val dashboard = dashboardService.saveDashboard(
                    SaveDashboardInput(
                        uuid = null,
                        name = name,
                        userScope = DashboardContextUserScope.PRIVATE,
                        widgets = listOf(
                            WidgetInstanceInput(
                                uuid = null,
                                key = "home/LastActiveProjects",
                                config = mapOf("count" to 10).asJson(),
                                layout = WidgetLayout(x = 0, y = 0, w = 12, h = 1),
                            )
                        ),
                        select = true,
                    )
                )
                val selectedDashboard = dashboardService.userDashboard()
                assertEquals(
                    dashboard,
                    selectedDashboard
                )
            }
        }
    }

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

    private fun withNoDashboard(code: () -> Unit) {
        asAdmin {
            val dashboards = dashboardService.userDashboards()
            dashboards.forEach {
                if (it.userScope != DashboardContextUserScope.BUILT_IN) {
                    dashboardService.deleteDashboard(it.uuid)
                }
            }
            code()
        }
    }

}