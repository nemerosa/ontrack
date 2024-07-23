package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTrackingService
import net.nemerosa.ontrack.it.waitFor
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GQLTypeAutoVersioningAuditEntryIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var autoVersioningTrackingService: AutoVersioningTrackingService

    @Autowired
    private lateinit var autoVersioningAuditQueryService: AutoVersioningAuditQueryService

    @Test
    fun `Getting the promotion run from the audit entry`() {
        withSimpleSetup { pl, target ->
            // Promoting
            val run = pl.run()
            // Gets the AV for this run
            val orderId = waitFor("Trail ready") {
                autoVersioningTrackingService.getTrail(run)?.branches?.firstOrNull()?.orderId
            } until { orderId ->
                val entry = autoVersioningAuditQueryService.findByUUID(target, orderId)
                entry != null
            }
            // Getting the audit entry for this promotion
            run(
                """
                {
                    autoVersioningAuditEntries(filter: {
                        uuid: "$orderId"
                    }) {
                        pageItems {
                            promotionRun {
                                id
                            }
                        }
                    }
                }
            """
            ) { data ->
                val runId = data.path("autoVersioningAuditEntries")
                    .path("pageItems").path(0)
                    .path("promotionRun").path("id")
                    .asInt()
                assertEquals(run.id(), runId, "Promotion run was retrieved from the audit entry")
            }
        }
    }

}