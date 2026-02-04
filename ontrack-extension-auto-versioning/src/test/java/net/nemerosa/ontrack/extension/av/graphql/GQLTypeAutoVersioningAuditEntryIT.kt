package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.common.waitFor
import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditState
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTrackingService
import net.nemerosa.ontrack.extension.queue.QueueNoAsync
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

@QueueNoAsync
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

    @Test
    fun `Getting a pull request from an audit entry`() {
        withSimpleSetup { pl, target ->
            // Promoting
            val run = pl.run()
            // Gets the AV for this run and waits for it to be done
            val orderId = waitFor("Trail ready") {
                autoVersioningTrackingService.getTrail(run)?.branches?.firstOrNull()?.orderId
            } until { orderId ->
                val entry = autoVersioningAuditQueryService.findByUUID(target, orderId)
                entry != null && entry.mostRecentState.state == AutoVersioningAuditState.PR_MERGED
            }
            // Getting the PR
            run(
                """
                {
                    autoVersioningAuditEntry(uuid: "$orderId") {
                        pullRequest {
                            id
                            name
                            link
                            status
                        }
                    }
                }
                """.trimIndent()
            ) { data ->
                val pr = data.path("autoVersioningAuditEntry").path("pullRequest")
                assertEquals("1", pr.path("id").asText())
                assertEquals("#1", pr.path("name").asText())
                assertEquals("mock:pr:1", pr.path("link").asText())
                assertEquals("MERGED", pr.path("status").asText())
            }
        }
    }

}