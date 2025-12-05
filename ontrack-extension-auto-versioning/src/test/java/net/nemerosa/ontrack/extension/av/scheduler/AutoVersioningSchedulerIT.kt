package net.nemerosa.ontrack.extension.av.scheduler

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditState
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditStore
import net.nemerosa.ontrack.extension.queue.QueueNoAsync
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@AsAdminTest
@QueueNoAsync
class AutoVersioningSchedulerIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var scheduler: AutoVersioningScheduler

    @Autowired
    private lateinit var store: AutoVersioningAuditStore

    @Autowired
    private lateinit var queryService: AutoVersioningAuditQueryService

    @BeforeEach
    fun init() {
        store.removeAll()
    }

    @Test
    fun `Unscheduled orders are taken`() {
        project {
            branch {
                val entry = createOrder("sourceProject").run {
                    store.create(this)
                }
                scheduler.schedule()
                // Checks the order is taken
                assertNotNull(
                    queryService.findByUUID(this, entry.order.uuid),
                    "Order audit entry found"
                ) {
                    assertNotEquals(
                        AutoVersioningAuditState.CREATED,
                        it.mostRecentState.state,
                    )
                }
            }
        }
    }

    @Test
    fun `Scheduled orders in the future are not taken`() {
        project {
            branch {
                val entry = createOrder(
                    sourceProject = "sourceProject",
                    schedule = Time.now.plusHours(1),
                ).run {
                    store.create(this)
                }
                scheduler.schedule()
                // Checks the order is NOT taken
                assertNotNull(
                    queryService.findByUUID(this, entry.order.uuid),
                    "Order audit entry found"
                ) {
                    assertEquals(
                        AutoVersioningAuditState.CREATED,
                        it.mostRecentState.state,
                    )
                }
            }
        }
    }

    @Test
    fun `Scheduled orders in the past are taken`() {
        project {
            branch {
                val entry = createOrder(
                    sourceProject = "sourceProject",
                    schedule = Time.now.minusHours(1),
                ).run {
                    store.create(this)
                }
                scheduler.schedule()
                // Checks the order is taken
                assertNotNull(
                    queryService.findByUUID(this, entry.order.uuid),
                    "Order audit entry found"
                ) {
                    assertNotEquals(
                        AutoVersioningAuditState.CREATED,
                        it.mostRecentState.state,
                    )
                }
            }
        }
    }

}