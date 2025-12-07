package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryFilter
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditState
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditStore
import net.nemerosa.ontrack.extension.queue.QueueNoAsync
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@TestPropertySource(
    properties = [
        // Disabling the scheduling job, so that we can control it explicitly
        "ontrack.extension.auto-versioning.scheduling.enabled=false",
    ]
)
@QueueNoAsync
class AutoVersioningDispatcherIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var store: AutoVersioningAuditStore

    @Autowired
    private lateinit var queryService: AutoVersioningAuditQueryService

    @Autowired
    private lateinit var dispatcher: AutoVersioningDispatcher

    @BeforeEach
    fun init() {
        store.removeAll()
    }

    @Test
    @AsAdminTest
    @Disabled("When dispatching the first order, the request cannot be throttled because it's processed synchronously.")
    fun `Throttling of unscheduled auto-versioning requests`() {
        withSimpleSetup { sourcePromotion, targetBranch ->
            // Creating two promotions
            sourcePromotion.branch.apply {
                build("1.0.0") {
                    promote(sourcePromotion)
                }
                build("2.0.0") {
                    promote(sourcePromotion)
                }
            }

            assertNotNull(
                queryService.findByFilter(
                    filter = AutoVersioningAuditQueryFilter(
                        project = targetBranch.project.name,
                        branch = targetBranch.name,
                        version = "1.0.0"
                    )
                ).firstOrNull(),
                "Entry 1.0.0 found"
            ) {
                assertEquals(
                    AutoVersioningAuditState.THROTTLED,
                    it.mostRecentState.state,
                    "Entry 1.0.0 is throttled",
                )
            }

            assertNotNull(
                queryService.findByFilter(
                    filter = AutoVersioningAuditQueryFilter(
                        project = targetBranch.project.name,
                        branch = targetBranch.name,
                        version = "2.0.0"
                    )
                ).firstOrNull(),
                "Entry 2.0.0 found"
            ) {
                assertEquals(
                    AutoVersioningAuditState.SCHEDULED,
                    it.mostRecentState.state,
                    "Entry 2.0.0 is scheduled"
                )
            }
        }
    }

    @Test
    @AsAdminTest
    fun `Throttling of scheduled auto-versioning requests`() {
        withSimpleSetup(
            cronSchedule = "0 0 23 * * *"
        ) { sourcePromotion, targetBranch ->
            // Creating two promotions
            sourcePromotion.branch.apply {
                build("1.0.0") {
                    promote(sourcePromotion)
                }
                build("2.0.0") {
                    promote(sourcePromotion)
                }
            }

            assertNotNull(
                queryService.findByFilter(
                    filter = AutoVersioningAuditQueryFilter(
                        project = targetBranch.project.name,
                        branch = targetBranch.name,
                        version = "1.0.0"
                    )
                ).firstOrNull(),
                "Entry 1.0.0 found"
            ) {
                assertEquals(
                    AutoVersioningAuditState.THROTTLED,
                    it.mostRecentState.state,
                    "Entry 1.0.0 is throttled"
                )
            }

            assertNotNull(
                queryService.findByFilter(
                    filter = AutoVersioningAuditQueryFilter(
                        project = targetBranch.project.name,
                        branch = targetBranch.name,
                        version = "2.0.0"
                    )
                ).firstOrNull(),
                "Entry 2.0.0 found"
            ) {
                assertEquals(
                    AutoVersioningAuditState.CREATED,
                    it.mostRecentState.state,
                    "Entry 2.0.0 is created, will be scheduled later"
                )
            }
        }
    }

    @Test
    @AsAdminTest
    fun `Rescheduling an order`() {
        project {
            branch {
                val entry = createOrder(sourceProject = "sourceProject", targetVersion = "1.1.0").run {
                    store.create(this)
                    store.addState(
                        uuid = uuid,
                        targetBranch = branch,
                        state = AutoVersioningAuditState.THROTTLED,
                    )
                    this
                }

                // Rescheduling
                val order = dispatcher.reschedule(entry.branch, entry.uuid)

                // Checks the order is rescheduled
                assertNotNull(
                    queryService.findByUUID(entry.branch, order.uuid),
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