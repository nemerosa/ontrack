package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTrackingService
import net.nemerosa.ontrack.extension.av.tracking.RejectedBranch
import net.nemerosa.ontrack.model.structure.PromotionLevel
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
                    trail.potentialTargetBranches
                )
                assertEquals(
                    emptyList(),
                    trail.rejectedTargetBranches,
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
                        app2,
                        app1,
                    ),
                    trail.potentialTargetBranches
                )
                assertEquals(
                    listOf(
                        RejectedBranch(
                            branch = app2,
                            reason = "Branch is disabled"
                        )
                    ),
                    trail.rejectedTargetBranches,
                )
            }
        }
    }

    private fun PromotionLevel.run() = branch.build().promote(this)

}