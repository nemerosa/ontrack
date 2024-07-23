package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTrackingService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AutoVersioningPromotionListenerServiceIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var autoVersioningTrackingService: AutoVersioningTrackingService

    @Test
    fun `Getting the trail of a promotion run`() {
        withPromotionLevelTargets { pl, app1, app2 ->
            val run = pl.run()
            assertNotNull(
                autoVersioningTrackingService.getTrail(run),
                "Trail registered"
            ) { trail ->
                assertEquals(
                    listOf(
                        app2,
                        app1,
                    ),
                    trail.branches.map { it.branch }
                )
                assertEquals(
                    emptyList(),
                    trail.branches.filter { !it.rejectionReason.isNullOrBlank() },
                )
            }
        }
    }

    @Test
    fun `Getting the trail of a promotion run with a disabled branch`() {
        withPromotionLevelTargets { pl, app1, app2 ->
            structureService.disableBranch(app2)
            val run = pl.run()
            assertNotNull(
                autoVersioningTrackingService.getTrail(run),
                "Trail registered"
            ) { trail ->
                assertEquals(
                    listOf(
                        app2.withDisabled(true),
                        app1,
                    ),
                    trail.branches.map { it.branch }
                )
                assertNotNull(trail.branches.find { it.branch.id == app1.id }, "App 1 trail") {
                    assertNull(it.rejectionReason, "Not rejected")
                }
                assertNotNull(trail.branches.find { it.branch.id == app2.id }, "App 2 trail") {
                    assertEquals("Branch is disabled", it.rejectionReason)
                }
            }
        }
    }

}