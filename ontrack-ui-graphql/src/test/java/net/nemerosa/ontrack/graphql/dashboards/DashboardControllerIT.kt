package net.nemerosa.ontrack.graphql.dashboards

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredBooleanField
import net.nemerosa.ontrack.json.getRequiredJsonField
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.model.dashboards.*
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.fail

class DashboardControllerIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var dashboardService: DashboardService

    @Test
    fun `Default dashboard`() {
        withNoDashboard {
            asUser {
                run(
                    """
                    {
                        userDashboards {
                            uuid
                        }
                    }
                """
                ) { data ->
                    val uuids = data.path("userDashboards").map { it.getRequiredTextField("uuid") }
                    assertEquals(listOf("0"), uuids)
                }
            }
        }
    }

    @Test
    fun `Default dashboard is the default dashboard`() {
        withNoDashboard {
            asUser {
                run(
                    """
                    {
                        userDashboard {
                            uuid
                        }
                    }
                """
                ) { data ->
                    val uuid = data.path("userDashboard").getRequiredTextField("uuid")
                    assertEquals("0", uuid)
                }
            }
        }
    }

    @Test
    fun `Creating a personal dashboard makes it the default dashboard`() {
        val name = TestUtils.uid("dash_")
        withNoDashboard {
            asUser().with(DashboardEdition::class.java).call {
                run(
                    """
                    mutation SaveDashboard {
                        saveDashboard(input: {
                            name: "$name",
                            userScope: PRIVATE,
                            widgets: {
                                key: "home/LastActiveProjects",
                                config: { count: 10 },
                                layout: { x: 0, y: 0, w: 12, h: 1 }
                            },
                            select: true,
                        }) {
                            dashboard {
                                uuid
                            }
                            errors {
                                message
                            }
                        }
                    }
                """
                ) { data ->
                    checkGraphQLUserErrors(data, "saveDashboard")
                    val uuid = data.path("saveDashboard").path("dashboard").getRequiredTextField("uuid")
                    run(
                        """
                        {
                            userDashboard {
                                uuid
                            }
                        }
                    """
                    ) { x ->
                        assertEquals(uuid, x.path("userDashboard").getRequiredTextField("uuid"))
                    }
                }
            }
        }
    }

    @Test
    fun `Getting the authorizations on the dashboards`() {
        val name = TestUtils.uid("dash_")
        withNoDashboard {
            val uuid = dashboardService.saveDashboard(
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
            ).uuid
            // Getting the authorizations
            run(
                """
                    {
                        userDashboards {
                            uuid
                            authorizations {
                                edit
                                share
                                delete
                            }
                        }
                    }
                """
            ) { data ->
                val dashboard = data.path("userDashboards")
                    .find { it.getRequiredTextField("uuid") == uuid }
                    ?: fail("Cannot find created dashboard")
                val authorizations = dashboard.getRequiredJsonField("authorizations")
                assertEquals(true, authorizations.getRequiredBooleanField("edit"))
                assertEquals(true, authorizations.getRequiredBooleanField("share"))
                assertEquals(true, authorizations.getRequiredBooleanField("delete"))
            }
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