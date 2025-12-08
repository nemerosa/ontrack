package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryFilter
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditState
import net.nemerosa.ontrack.extension.queue.QueueNoAsync
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@QueueNoAsync
class AutoVersioningMutationRescheduleIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var autoVersioningAuditQueryService: AutoVersioningAuditQueryService

    @Test
    @AsAdminTest
    fun `Rescheduling an order`() {
        withSimpleSetup { sourcePromotion, targetBranch ->

            // Launching an AV request
            sourcePromotion.branch.apply {
                build("1.0.0") {
                    promote(sourcePromotion)
                }
            }

            val entry = autoVersioningAuditQueryService.findByFilter(
                filter = AutoVersioningAuditQueryFilter(
                    project = targetBranch.project.name,
                    branch = targetBranch.name,
                    version = "1.0.0"
                )
            ).firstOrNull()

            assertNotNull(entry, "Entry 1.0.0 found") {
                assertEquals(
                    AutoVersioningAuditState.PR_MERGED,
                    it.mostRecentState.state,
                    "Entry 1.0.0 has been merged",
                )
            }

            // Rescheduling the order
            run(
                """
                    mutation {
                        rescheduleAutoVersioning(input: {
                            uuid: "${entry.order.uuid}"
                        }) {
                            order {
                                uuid
                            }
                            errors {
                                message
                            }
                        }
                    }
                """.trimIndent()
            ) { data ->
                checkGraphQLUserErrors(data, "rescheduleAutoVersioning") { node ->
                    val uuid = node.path("order").path("uuid").asText()
                    // Was rescheduled, but aborted since same version
                    assertNotNull(
                        autoVersioningAuditQueryService.findByUUID(targetBranch, uuid),
                        "Rescheduled entry 1.0.0 found"
                    ) {
                        assertEquals(
                            AutoVersioningAuditState.PROCESSING_ABORTED,
                            it.mostRecentState.state,
                            "Rescheduled entry 1.0.0 has been aborted (same version)",
                        )
                    }
                }
            }

        }
    }

}