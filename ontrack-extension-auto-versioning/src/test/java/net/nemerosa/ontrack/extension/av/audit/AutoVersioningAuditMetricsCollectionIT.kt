package net.nemerosa.ontrack.extension.av.audit

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class AutoVersioningAuditMetricsCollectionIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var autoVersioningAuditService: AutoVersioningAuditService

    @Autowired
    private lateinit var autoVersioningAuditCleanupService: AutoVersioningAuditCleanupService

    @Autowired
    private lateinit var meterRegistry: MeterRegistry

    @Test
    fun `Metrics per state`() {
        val source = project()
        project {
            branch {
                // Purge existing audit entries
                autoVersioningAuditCleanupService.purge()
                // In queue
                create(source) {
                    autoVersioningAuditService.onQueuing(it, "routing")
                }
                // Received
                repeat(2) {
                    create(source) {
                        autoVersioningAuditService.onQueuing(it, "routing")
                        autoVersioningAuditService.onReceived(it, "queue")
                    }
                }
                // Error
                val error = RuntimeException("test")
                repeat(3) {
                    create(source) {
                        autoVersioningAuditService.onQueuing(it, "routing")
                        autoVersioningAuditService.onReceived(it, "queue")
                        autoVersioningAuditService.onError(it, error)
                    }
                }
                // Processing start
                repeat(4) {
                    create(source) {
                        autoVersioningAuditService.onQueuing(it, "routing")
                        autoVersioningAuditService.onReceived(it, "queue")
                        autoVersioningAuditService.onProcessingStart(it)
                    }
                }
                // Processing aborted
                repeat(5) {
                    create(source) {
                        autoVersioningAuditService.onQueuing(it, "routing")
                        autoVersioningAuditService.onReceived(it, "queue")
                        autoVersioningAuditService.onProcessingStart(it)
                        autoVersioningAuditService.onProcessingAborted(it, "test")
                    }
                }
                // Processing creating branch
                repeat(6) {
                    create(source) {
                        autoVersioningAuditService.onQueuing(it, "routing")
                        autoVersioningAuditService.onReceived(it, "queue")
                        autoVersioningAuditService.onProcessingStart(it)
                        autoVersioningAuditService.onProcessingCreatingBranch(it, "branch")
                    }
                }
                // Processing updating file
                repeat(7) {
                    create(source) {
                        autoVersioningAuditService.onQueuing(it, "routing")
                        autoVersioningAuditService.onReceived(it, "queue")
                        autoVersioningAuditService.onProcessingStart(it)
                        autoVersioningAuditService.onProcessingCreatingBranch(it, "branch")
                        autoVersioningAuditService.onProcessingUpdatingFile(it, "branch", "file")
                    }
                }
                // Processing post processing start
                repeat(8) {
                    create(source) {
                        autoVersioningAuditService.onQueuing(it, "routing")
                        autoVersioningAuditService.onReceived(it, "queue")
                        autoVersioningAuditService.onProcessingStart(it)
                        autoVersioningAuditService.onProcessingCreatingBranch(it, "branch")
                        autoVersioningAuditService.onProcessingUpdatingFile(it, "branch", "file")
                        autoVersioningAuditService.onPostProcessingStart(it, "branch")
                    }
                }
                // Processing post processing end
                repeat(9) {
                    create(source) {
                        autoVersioningAuditService.onQueuing(it, "routing")
                        autoVersioningAuditService.onReceived(it, "queue")
                        autoVersioningAuditService.onProcessingStart(it)
                        autoVersioningAuditService.onProcessingCreatingBranch(it, "branch")
                        autoVersioningAuditService.onProcessingUpdatingFile(it, "branch", "file")
                        autoVersioningAuditService.onPostProcessingStart(it, "branch")
                        autoVersioningAuditService.onPostProcessingEnd(it, "branch")
                    }
                }
                // PR creating
                repeat(10) {
                    create(source) {
                        autoVersioningAuditService.onQueuing(it, "routing")
                        autoVersioningAuditService.onReceived(it, "queue")
                        autoVersioningAuditService.onProcessingStart(it)
                        autoVersioningAuditService.onProcessingCreatingBranch(it, "branch")
                        autoVersioningAuditService.onProcessingUpdatingFile(it, "branch", "file")
                        autoVersioningAuditService.onPRCreating(it, "branch")
                    }
                }
                // PR merged
                repeat(11) {
                    create(source) {
                        autoVersioningAuditService.onQueuing(it, "routing")
                        autoVersioningAuditService.onReceived(it, "queue")
                        autoVersioningAuditService.onProcessingStart(it)
                        autoVersioningAuditService.onProcessingCreatingBranch(it, "branch")
                        autoVersioningAuditService.onProcessingUpdatingFile(it, "branch", "file")
                        autoVersioningAuditService.onPRCreating(it, "branch")
                        autoVersioningAuditService.onPRMerged(it, "branch", "#1", "uri:1")
                    }
                }
                // PR created
                repeat(12) {
                    create(source) {
                        autoVersioningAuditService.onQueuing(it, "routing")
                        autoVersioningAuditService.onReceived(it, "queue")
                        autoVersioningAuditService.onProcessingStart(it)
                        autoVersioningAuditService.onProcessingCreatingBranch(it, "branch")
                        autoVersioningAuditService.onProcessingUpdatingFile(it, "branch", "file")
                        autoVersioningAuditService.onPRCreating(it, "branch")
                        autoVersioningAuditService.onPRCreated(it, "branch", "#1", "uri:1")
                    }
                }
                // PR approved
                repeat(13) {
                    create(source) {
                        autoVersioningAuditService.onQueuing(it, "routing")
                        autoVersioningAuditService.onReceived(it, "queue")
                        autoVersioningAuditService.onProcessingStart(it)
                        autoVersioningAuditService.onProcessingCreatingBranch(it, "branch")
                        autoVersioningAuditService.onProcessingUpdatingFile(it, "branch", "file")
                        autoVersioningAuditService.onPRCreating(it, "branch")
                        autoVersioningAuditService.onPRApproved(it, "branch", "#1", "uri:1")
                    }
                }
                // PR timeout
                repeat(14) {
                    create(source) {
                        autoVersioningAuditService.onQueuing(it, "routing")
                        autoVersioningAuditService.onReceived(it, "queue")
                        autoVersioningAuditService.onProcessingStart(it)
                        autoVersioningAuditService.onProcessingCreatingBranch(it, "branch")
                        autoVersioningAuditService.onProcessingUpdatingFile(it, "branch", "file")
                        autoVersioningAuditService.onPRCreating(it, "branch")
                        autoVersioningAuditService.onPRTimeout(it, "branch", "#1", "uri:1")
                    }
                }
                // Collects the metrics
                val gauges =
                    meterRegistry.find(AutoVersioningAuditMetrics.autoVersioningAuditState)
                        .gauges()
                val taggedGauges = gauges.associateBy {
                    val tag = it.id.getTag("state") ?: throw IllegalStateException("Null tag")
                    AutoVersioningAuditState.valueOf(tag)
                }
                val stateValues = taggedGauges.mapValues { (_, gauge) ->
                    gauge.value()
                }
                assertEquals(
                    mapOf(
                        AutoVersioningAuditState.CREATED to 1.0,
                        AutoVersioningAuditState.RECEIVED to 2.0,
                        AutoVersioningAuditState.ERROR to 3.0,
                        AutoVersioningAuditState.PROCESSING_START to 4.0,
                        AutoVersioningAuditState.PROCESSING_ABORTED to 5.0,
                        AutoVersioningAuditState.PROCESSING_CREATING_BRANCH to 6.0,
                        AutoVersioningAuditState.PROCESSING_UPDATING_FILE to 7.0,
                        AutoVersioningAuditState.POST_PROCESSING_START to 8.0,
                        AutoVersioningAuditState.POST_PROCESSING_END to 9.0,
                        AutoVersioningAuditState.PR_CREATING to 10.0,
                        AutoVersioningAuditState.PR_MERGED to 11.0,
                        AutoVersioningAuditState.PR_CREATED to 12.0,
                        AutoVersioningAuditState.PR_APPROVED to 13.0,
                        AutoVersioningAuditState.PR_TIMEOUT to 14.0,
                    ),
                    stateValues
                )
            }
        }
    }

    private fun Branch.create(source: Project, code: (AutoVersioningOrder) -> Unit) {
        code(createOrder(sourceProject = source.name))
    }

}