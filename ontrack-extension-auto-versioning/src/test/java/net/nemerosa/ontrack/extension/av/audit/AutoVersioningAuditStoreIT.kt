package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AutoVersioningAuditStoreIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var autoVersioningAuditStore: AutoVersioningAuditStore

    @Test
    fun `Cancelling previous orders must distinguish between paths`() {
        asAdmin {
            project {
                branch {
                    // Adding an order
                    val original = autoVersioningAuditStore.create(
                        createOrder(sourceProject = "source", targetPaths = listOf("one.yaml")),
                        routing = "some-routing"
                    )
                    // Adding a similar order with a different path
                    val otherPath = autoVersioningAuditStore.create(
                        createOrder(sourceProject = "source", targetPaths = listOf("two.yaml")),
                        routing = "some-routing"
                    )
                    // Trying to cancel all orders and queuing the same order as the original
                    autoVersioningAuditStore.cancelQueuedOrders(
                        createOrder(sourceProject = "source", targetPaths = listOf("one.yaml")),
                    )
                    // The original order must have been cancelled
                    assertNotNull(autoVersioningAuditStore.findByUUID(this, original.uuid), "Original order") {
                        assertEquals(AutoVersioningAuditState.PROCESSING_CANCELLED, it.mostRecentState.state, "Similar order must be cancelled")
                    }
                    // The order with a different path must have been kept
                    assertNotNull(autoVersioningAuditStore.findByUUID(this, otherPath.uuid), "Other order") {
                        assertEquals(AutoVersioningAuditState.CREATED, it.mostRecentState.state, "Different path order must be kept")
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
                    val original = autoVersioningAuditStore.create(
                        createOrder(sourceProject = "source", targetPaths = listOf("one.yaml", "two.yaml")),
                        routing = "some-routing"
                    )
                    // Adding a similar order with a different path
                    val otherPath = autoVersioningAuditStore.create(
                        createOrder(sourceProject = "source", targetPaths = listOf("one.yaml")),
                        routing = "some-routing"
                    )
                    // Trying to cancel all orders and queuing the same order as the original
                    autoVersioningAuditStore.cancelQueuedOrders(
                        createOrder(sourceProject = "source", targetPaths = listOf("one.yaml", "two.yaml")),
                    )
                    // The original order must have been cancelled
                    assertNotNull(autoVersioningAuditStore.findByUUID(this, original.uuid), "Original order") {
                        assertEquals(AutoVersioningAuditState.PROCESSING_CANCELLED, it.mostRecentState.state, "Similar order must be cancelled")
                    }
                    // The order with a different path must have been kept
                    assertNotNull(autoVersioningAuditStore.findByUUID(this, otherPath.uuid), "Other order") {
                        assertEquals(AutoVersioningAuditState.CREATED, it.mostRecentState.state, "Different path order must be kept")
                    }
                }
            }
        }
    }

}