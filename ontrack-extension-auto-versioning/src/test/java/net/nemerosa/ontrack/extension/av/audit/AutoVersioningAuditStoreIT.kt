package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class AutoVersioningAuditStoreIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var autoVersioningAuditStore: AutoVersioningAuditStore

    @Autowired
    private lateinit var autoVersioningAuditService: AutoVersioningAuditService

    @BeforeEach
    fun clear() {
        autoVersioningAuditStore.removeAll()
    }

    @Test
    fun `Cancelling previous orders must distinguish between paths`() {
        asAdmin {
            project {
                branch {
                    // Adding an order
                    val original =
                        createOrder(sourceProject = "source", targetPaths = listOf("one.yaml")).apply {
                            autoVersioningAuditStore.create(this)
                        }
                    // Adding a similar order with a different path
                    val otherPath =
                        createOrder(sourceProject = "source", targetPaths = listOf("two.yaml")).apply {
                            autoVersioningAuditStore.create(this)
                        }
                    // Trying to cancel all orders and queuing the same order as the original
                    autoVersioningAuditStore.throttling(
                        createOrder(sourceProject = "source", targetPaths = listOf("one.yaml")),
                    )
                    // The original order must have been cancelled
                    assertNotNull(autoVersioningAuditStore.findByUUID(this, original.uuid), "Original order") {
                        assertEquals(
                            AutoVersioningAuditState.THROTTLED,
                            it.mostRecentState.state,
                            "Similar order must be cancelled"
                        )
                    }
                    // The order with a different path must have been kept
                    assertNotNull(autoVersioningAuditStore.findByUUID(this, otherPath.uuid), "Other order") {
                        assertEquals(
                            AutoVersioningAuditState.CREATED,
                            it.mostRecentState.state,
                            "Different path order must be kept"
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Cancelling previous orders must distinguish between paths even for multiple paths`() {
        asAdmin {
            project {
                branch {
                    // Adding an order
                    val original =
                        createOrder(sourceProject = "source", targetPaths = listOf("one.yaml", "two.yaml")).apply {
                            autoVersioningAuditStore.create(this)
                        }
                    // Adding a similar order with a different path
                    val otherPath =
                        createOrder(sourceProject = "source", targetPaths = listOf("one.yaml")).apply {
                            autoVersioningAuditStore.create(this)
                        }
                    // Trying to cancel all orders and queuing the same order as the original
                    autoVersioningAuditStore.throttling(
                        createOrder(sourceProject = "source", targetPaths = listOf("one.yaml", "two.yaml")),
                    )
                    // The original order must have been cancelled
                    assertNotNull(autoVersioningAuditStore.findByUUID(this, original.uuid), "Original order") {
                        assertEquals(
                            AutoVersioningAuditState.THROTTLED,
                            it.mostRecentState.state,
                            "Similar order must be cancelled"
                        )
                    }
                    // The order with a different path must have been kept
                    assertNotNull(autoVersioningAuditStore.findByUUID(this, otherPath.uuid), "Other order") {
                        assertEquals(
                            AutoVersioningAuditState.CREATED,
                            it.mostRecentState.state,
                            "Different path order must be kept"
                        )
                    }
                }
            }
        }
    }

    @Test
    @AsAdminTest
    fun `Saving the most recent state and timestamp`() {
        val now = LocalDateTime.now()
        project {
            branch {
                val order = autoVersioningAuditStore.withSignatureDaysOlder(securityService, 20) {
                    createOrder(sourceProject = "source").apply {
                        autoVersioningAuditStore.create(this)
                    }
                }
                // Getting the initial state
                val entry = autoVersioningAuditStore.findByUUID(this, order.uuid)
                    ?: fail("Could not find saved entry")
                assertEquals(AutoVersioningAuditState.CREATED, entry.mostRecentState.state)
                // Timestamp older than 19 days
                assertTrue(
                    entry.timestamp.isBefore(now.minusDays(19)),
                    "Entry timestamp should be older than 19 days"
                )

                // Listing all entries
                autoVersioningAuditStore.auditVersioningEntries(
                    filter = AutoVersioningAuditQueryFilter(
                        branch = this.name,
                        project = this.project.name,
                        count = 100,
                    )
                ).forEach { entry -> println("[${entry.timestamp}] $entry") }

                // Querying on the date
                assertEquals(
                    1,
                    autoVersioningAuditStore.findAllBefore(now.minusDays(19), nonRunningOnly = false).size
                )
                assertEquals(
                    1,
                    autoVersioningAuditStore.findAllBefore(now.minusDays(9), nonRunningOnly = false).size
                )

                // Adding a more recent state
                autoVersioningAuditStore.withSignatureDaysOlder(securityService, 10) {
                    autoVersioningAuditStore.addState(
                        targetBranch = this,
                        uuid = order.uuid,
                        state = AutoVersioningAuditState.SCHEDULED,
                    )
                    // Getting the new state
                    val entry = autoVersioningAuditStore.findByUUID(this, order.uuid)
                        ?: fail("Could not find saved entry")
                    assertEquals(AutoVersioningAuditState.SCHEDULED, entry.mostRecentState.state)
                    // Timestamp older than 9 days
                    assertTrue(
                        entry.timestamp.isBefore(now.minusDays(9)),
                        "Entry timestamp should be older than 9 days"
                    )
                    // ... but more recent than 19 days
                    assertTrue(
                        entry.timestamp.isAfter(now.minusDays(19)),
                        "Entry timestamp should be more recent than 19 days"
                    )
                }

                // Listing all entries
                autoVersioningAuditStore.auditVersioningEntries(
                    filter = AutoVersioningAuditQueryFilter(
                        branch = this.name,
                        project = this.project.name,
                        count = 100,
                    )
                ).forEach { entry -> println("[${entry.timestamp}] $entry") }

                // Querying on the date
                assertEquals(
                    0,
                    autoVersioningAuditStore.findAllBefore(now.minusDays(19), nonRunningOnly = false).size
                )
                assertEquals(
                    1,
                    autoVersioningAuditStore.findAllBefore(now.minusDays(9), nonRunningOnly = false).size
                )
            }
        }
    }

    @Test
    fun `Filtering by date and running state`() {
        project {
            branch {
                val oldRunningOrder = autoVersioningAuditStore.withSignatureDaysOlder(securityService, 10) {
                    createOrder(sourceProject = "source").apply {
                        autoVersioningAuditService.onCreated(this)
                    }
                }
                val oldStoppedOrder = autoVersioningAuditStore.withSignatureDaysOlder(securityService, 10) {
                    createOrder(sourceProject = "source").apply {
                        autoVersioningAuditService.onCreated(this)
                        autoVersioningAuditService.onPRMerged(this, "branch", "#1", "uri:1")
                    }
                }
                val recentRunningOrder = autoVersioningAuditStore.withSignatureDaysOlder(securityService, 5) {
                    createOrder(sourceProject = "source").apply {
                        autoVersioningAuditService.onCreated(this)
                    }
                }
                val recentStoppedOrder = autoVersioningAuditStore.withSignatureDaysOlder(securityService, 5) {
                    createOrder(sourceProject = "source").apply {
                        autoVersioningAuditService.onCreated(this)
                        autoVersioningAuditService.onPRMerged(this, "branch", "#1", "uri:1")
                    }
                }

                // All stopped orders
                assertEquals(
                    setOf(oldStoppedOrder.uuid, recentStoppedOrder.uuid),
                    autoVersioningAuditStore.findAllBefore(
                        LocalDateTime.now().plusMinutes(1),
                        nonRunningOnly = true
                    ).map { it.order.uuid }.toSet(),
                    "All stopped orders"
                )

                // All orders
                assertEquals(
                    setOf(oldStoppedOrder.uuid, recentStoppedOrder.uuid, oldRunningOrder.uuid, recentRunningOrder.uuid),
                    autoVersioningAuditStore.findAllBefore(
                        LocalDateTime.now().plusMinutes(1),
                        nonRunningOnly = false
                    ).map { it.order.uuid }.toSet(),
                    "All orders"
                )

                // Old stopped orders
                assertEquals(
                    setOf(oldStoppedOrder.uuid),
                    autoVersioningAuditStore.findAllBefore(
                        LocalDateTime.now().minusDays(7),
                        nonRunningOnly = true
                    ).map { it.order.uuid }.toSet(),
                    "Old stopped orders"
                )

                // Old orders
                assertEquals(
                    setOf(oldStoppedOrder.uuid, oldRunningOrder.uuid),
                    autoVersioningAuditStore.findAllBefore(
                        LocalDateTime.now().minusDays(7),
                        nonRunningOnly = false
                    ).map { it.order.uuid }.toSet(),
                    "Old orders"
                )
            }
        }
    }

    @Test
    @AsAdminTest
    fun `Unscheduled orders are ready`() {
        autoVersioningAuditStore.removeAll()
        project {
            branch {
                val order = createOrder(sourceProject = "source").apply {
                    autoVersioningAuditService.onCreated(this)
                }
                val entries = autoVersioningAuditStore.findByReady(Time.now)
                assertEquals(
                    listOf(order.uuid),
                    entries.map { it.order.uuid },
                )
            }
        }
    }

    @Test
    @AsAdminTest
    fun `Scheduled orders in the future are not ready`() {
        autoVersioningAuditStore.removeAll()
        project {
            branch {
                createOrder(
                    sourceProject = "source",
                    schedule = Time.now.plusHours(1),
                ).apply {
                    autoVersioningAuditService.onCreated(this)
                }
                val entries = autoVersioningAuditStore.findByReady(Time.now)
                assertTrue(entries.isEmpty(), "Scheduled orders in the future are not ready")
            }
        }
    }

    @Test
    @AsAdminTest
    fun `Scheduled orders in the past are ready`() {
        autoVersioningAuditStore.removeAll()
        project {
            branch {
                val order = createOrder(
                    sourceProject = "source",
                    schedule = Time.now.minusHours(1),
                ).apply {
                    autoVersioningAuditService.onCreated(this)
                }
                val entries = autoVersioningAuditStore.findByReady(Time.now)
                assertEquals(
                    listOf(order.uuid),
                    entries.map { it.order.uuid },
                )
            }
        }
    }

}