package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class AutoVersioningAuditQueryServiceIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var autoVersioningAuditService: AutoVersioningAuditService

    @Autowired
    private lateinit var autoVersioningAuditQueryService: AutoVersioningAuditQueryService

    @Autowired
    private lateinit var autoVersioningAuditCleanupService: AutoVersioningAuditCleanupService

    @Test
    fun `Last orders for a branch`() {
        val source = project()
        project {
            branch {
                val orders = (1..20).map {
                    createOrder(sourceProject = source.name, targetVersion = "2.0.$it").apply {
                        autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                        autoVersioningAuditService.onReceived(this, "queue")
                    }
                }
                val entries = autoVersioningAuditQueryService.findByFilter(
                    AutoVersioningAuditQueryFilter(
                        project = project.name, branch = name
                    )
                )
                val actualVersions = entries.map { it.order.targetVersion }
                val expectedVersions = orders.takeLast(10).reversed().map { it.targetVersion }
                assertEquals(expectedVersions, actualVersions, "Returned the last 10 orders for this branch")
            }
        }
    }

    @Test
    fun `Last five orders for a branch`() {
        val source = project()
        project {
            branch {
                val orders = (1..20).map {
                    createOrder(sourceProject = source.name, targetVersion = "2.0.$it").apply {
                        autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                        autoVersioningAuditService.onReceived(this, "queue")
                    }
                }
                val entries = autoVersioningAuditQueryService.findByFilter(
                    AutoVersioningAuditQueryFilter(
                        project = project.name, branch = name,
                        count = 5
                    )
                )
                val actualVersions = entries.map { it.order.targetVersion }
                val expectedVersions = orders.takeLast(5).reversed().map { it.targetVersion }
                assertEquals(expectedVersions, actualVersions, "Returned the last 10 orders for this branch")
            }
        }
    }

    @Test
    fun `Offset orders for a branch`() {
        val source = project()
        project {
            branch {
                val orders = (1..20).map {
                    createOrder(sourceProject = source.name, targetVersion = "2.0.$it").apply {
                        autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                        autoVersioningAuditService.onReceived(this, "queue")
                    }
                }
                val entries = autoVersioningAuditQueryService.findByFilter(
                    AutoVersioningAuditQueryFilter(
                        project = project.name, branch = name,
                        offset = 10,
                        count = 5
                    )
                )
                val actualVersions = entries.map { it.order.targetVersion }
                val expectedVersions = orders.dropLast(10).takeLast(5).reversed().map { it.targetVersion }
                assertEquals(
                    expectedVersions,
                    actualVersions,
                    "Returned the last 5 orders after the last 10 for this branch"
                )
            }
        }
    }

    @Test
    fun `Filter order on UUID for a branch`() {
        val source = project()
        project {
            branch {
                val orders = (1..20).map {
                    createOrder(sourceProject = source.name, targetVersion = "2.0.$it").apply {
                        autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                        autoVersioningAuditService.onReceived(this, "queue")
                    }
                }
                val entries = autoVersioningAuditQueryService.findByFilter(
                    AutoVersioningAuditQueryFilter(
                        project = project.name, branch = name,
                        uuid = orders[10].uuid
                    )
                )
                val actualVersions = entries.map { it.order.targetVersion }
                val expectedVersions = listOf(orders[10].targetVersion)
                assertEquals(expectedVersions, actualVersions)
            }
        }
    }

    @Test
    fun `Filter order on version for a branch`() {
        val source = project()
        project {
            branch {
                val orders = (1..20).map {
                    createOrder(sourceProject = source.name, targetVersion = "2.0.$it").apply {
                        autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                        autoVersioningAuditService.onReceived(this, "queue")
                    }
                }
                val entries = autoVersioningAuditQueryService.findByFilter(
                    AutoVersioningAuditQueryFilter(
                        project = project.name, branch = name,
                        version = orders[10].targetVersion
                    )
                )
                val actualVersions = entries.map { it.order.targetVersion }
                val expectedVersions = listOf(orders[10].targetVersion)
                assertEquals(expectedVersions, actualVersions)
            }
        }
    }

    @Test
    fun `Filter orders on states for a branch`() {
        val source = project()
        project {
            branch {
                val orders = (1..20).map {
                    createOrder(sourceProject = source.name, targetVersion = "2.0.$it").apply {
                        autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                        if (it % 2 == 0) {
                            autoVersioningAuditService.onReceived(this, "queue")
                        }
                    }
                }
                val entries = autoVersioningAuditQueryService.findByFilter(
                    AutoVersioningAuditQueryFilter(
                        project = project.name, branch = name,
                        state = AutoVersioningAuditState.CREATED
                    )
                )
                val actualVersions = entries.map { it.order.targetVersion }
                val expectedVersions =
                    orders.filterIndexed { index, _ -> (index + 1) % 2 != 0 }.reversed().map { it.targetVersion }
                assertEquals(expectedVersions, actualVersions)
            }
        }
    }

    @Test
    fun `Filter orders on state and version for a branch`() {
        val source = project()
        project {
            branch {
                val orders = (1..20).map {
                    createOrder(sourceProject = source.name, targetVersion = "2.0.$it").apply {
                        autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                        if (it % 2 == 0) {
                            autoVersioningAuditService.onReceived(this, "queue")
                        }
                    }
                }
                val entries = autoVersioningAuditQueryService.findByFilter(
                    AutoVersioningAuditQueryFilter(
                        project = project.name, branch = name,
                        state = AutoVersioningAuditState.CREATED,
                        version = orders[8].targetVersion
                    )
                )
                val actualVersions = entries.map { it.order.targetVersion }
                val expectedVersions = listOf(orders[8].targetVersion)
                assertEquals(expectedVersions, actualVersions)
            }
        }
    }

    @Test
    fun `Filter orders on source project for a branch`() {
        val sourceA = project()
        val sourceB = project()
        project {
            branch {
                (1..5).forEach {
                    createOrder(sourceProject = sourceA.name, targetVersion = "2.0.$it").apply {
                        autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                    }
                }
                val ordersB = (1..5).map {
                    createOrder(sourceProject = sourceB.name, targetVersion = "3.0.$it").apply {
                        autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                    }
                }
                val entries = autoVersioningAuditQueryService.findByFilter(
                    AutoVersioningAuditQueryFilter(
                        project = project.name, branch = name,
                        source = sourceB.name
                    )
                )
                val actualVersions = entries.map { it.order.targetVersion }
                val expectedVersions = ordersB.reversed().map { it.targetVersion }
                assertEquals(expectedVersions, actualVersions)
            }
        }
    }

    @Test
    fun `Filter orders for a project`() {
        val source = project()
        project {
            val orders = mutableListOf<AutoVersioningOrder>()
            branch {
                orders += (1..5).map {
                    createOrder(sourceProject = source.name, targetVersion = "2.0.$it").apply {
                        autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                    }
                }
            }
            branch {
                orders += (1..5).map {
                    createOrder(sourceProject = source.name, targetVersion = "3.0.$it").apply {
                        autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                    }
                }
            }

            autoVersioningAuditQueryService.findByFilter(
                AutoVersioningAuditQueryFilter(
                    project = name, branch = null
                )
            ).let { entries ->
                val actualVersions = entries.map { it.order.targetVersion }
                val expectedVersions = orders.reversed().map { it.targetVersion }
                assertEquals(expectedVersions, actualVersions)
            }

            autoVersioningAuditQueryService.findByFilter(
                AutoVersioningAuditQueryFilter(
                    project = name, branch = null,
                    count = 7
                )
            ).let { entries ->
                val actualVersions = entries.map { it.order.targetVersion }
                val expectedVersions = orders.reversed().take(7).map { it.targetVersion }
                assertEquals(expectedVersions, actualVersions)
            }

            autoVersioningAuditQueryService.findByFilter(
                AutoVersioningAuditQueryFilter(
                    project = name, branch = null,
                    version = orders[2].targetVersion
                )
            ).let { entries ->
                val actualVersions = entries.map { it.order.targetVersion }
                val expectedVersions = listOf(orders[2].targetVersion)
                assertEquals(expectedVersions, actualVersions)
            }
        }
    }

    @Test
    fun `Last orders on a project on several branches`() {
        val source = project()
        project {
            val branch1 = branch()
            val branch2 = branch()
            val orders = mutableListOf<AutoVersioningOrder>()
            (1..20).forEach {
                orders += if (it % 2 == 0) {
                    branch1.createOrder(sourceProject = source.name, targetVersion = "1.0.$it").apply {
                        autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                        autoVersioningAuditService.onReceived(this, "queue")
                    }
                } else {
                    branch2.createOrder(sourceProject = source.name, targetVersion = "2.0.$it").apply {
                        autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                        autoVersioningAuditService.onReceived(this, "queue")
                    }
                }
            }
            val entries = autoVersioningAuditQueryService.findByFilter(
                AutoVersioningAuditQueryFilter(
                    project = name
                )
            )
            val actualVersions = entries.map { it.order.targetVersion }
            val expectedVersions = orders.takeLast(10).reversed().map { it.targetVersion }
            assertEquals(expectedVersions, actualVersions, "Returned the last 10 orders for this project")
        }
    }

    @Test
    fun `Last orders on a all projects on several branches`() {
        val source = project()

        // Clears all audit entries
        autoVersioningAuditCleanupService.purge()

        val project1 = project()
        val project2 = project()

        val branch1 = project1.branch()
        val branch2 = project2.branch()


        val orders = mutableListOf<AutoVersioningOrder>()
        (1..20).forEach {
            orders += if (it % 2 == 0) {
                branch1.createOrder(sourceProject = source.name, targetVersion = "1.0.$it").apply {
                    autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                    autoVersioningAuditService.onReceived(this, "queue")
                }
            } else {
                branch2.createOrder(sourceProject = source.name, targetVersion = "2.0.$it").apply {
                    autoVersioningAuditService.onQueuing(this, "routing", cancelling = false)
                    autoVersioningAuditService.onReceived(this, "queue")
                }
            }
        }
        val entries = withGrantViewToAll {
            asUser {
                autoVersioningAuditQueryService.findByFilter(AutoVersioningAuditQueryFilter())
            }
        }
        val actualVersions = entries.map { it.order.targetVersion }
        val expectedVersions = orders.takeLast(10).reversed().map { it.targetVersion }
        assertEquals(expectedVersions, actualVersions, "Returned the last 10 orders for all projects")
    }
}