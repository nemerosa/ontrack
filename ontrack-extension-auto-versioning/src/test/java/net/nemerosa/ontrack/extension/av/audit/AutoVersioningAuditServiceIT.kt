package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.common.reducedStackTrace
import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditTestFixtures.assertAudit
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditTestFixtures.audit
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.model.structure.Branch
import org.apache.commons.lang3.exception.ExceptionUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AutoVersioningAuditServiceIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var autoVersioningAuditService: AutoVersioningAuditService

    @Autowired
    private lateinit var autoVersioningAuditQueryService: AutoVersioningAuditQueryService

    @Test
    fun `Successful auto versioning with no post processing`() {
        val source = project()
        project {
            branch {
                val order = createOrder(
                    sourceProject = source.name
                )
                autoVersioningAuditService.onQueuing(order, "routing")
                autoVersioningAuditService.onReceived(order, "queue")
                autoVersioningAuditService.onProcessingStart(order)
                autoVersioningAuditService.onProcessingCreatingBranch(order, "feature/version-2.0.0")
                autoVersioningAuditService.onProcessingUpdatingFile(order, "feature/version-2.0.0", "gradle.properties")
                autoVersioningAuditService.onPRCreating(order, "feature/version-2.0.0")
                autoVersioningAuditService.onPRMerged(order, "feature/version-2.0.0", "#1", "uri:1")

                autoVersioningAuditQueryService.getByUUID(this, order.uuid).let { entry ->
                    assertEquals(order, entry.order)
                    assertEquals(AutoVersioningAuditState.PR_MERGED, entry.mostRecentState.state)
                    assertAudit(
                        entry,
                        audit(
                            AutoVersioningAuditState.PR_MERGED,
                            "branch" to "feature/version-2.0.0",
                            "prName" to "#1",
                            "prLink" to "uri:1"
                        ),
                        audit(AutoVersioningAuditState.PR_CREATING, "branch" to "feature/version-2.0.0"),
                        audit(
                            AutoVersioningAuditState.PROCESSING_UPDATING_FILE,
                            "branch" to "feature/version-2.0.0",
                            "path" to "gradle.properties"
                        ),
                        audit(AutoVersioningAuditState.PROCESSING_CREATING_BRANCH, "branch" to "feature/version-2.0.0"),
                        audit(AutoVersioningAuditState.PROCESSING_START),
                        audit(AutoVersioningAuditState.RECEIVED),
                        audit(AutoVersioningAuditState.CREATED)
                    )
                }
            }
        }
    }

    @Test
    fun `Successful auto versioning with post processing`() {
        val source = project()
        project {
            branch {
                val order = createOrder(
                    sourceProject = source.name
                )
                autoVersioningAuditService.onQueuing(order, "routing")
                autoVersioningAuditService.onReceived(order, "queue")
                autoVersioningAuditService.onProcessingStart(order)
                autoVersioningAuditService.onProcessingCreatingBranch(order, "feature/version-2.0.0")
                autoVersioningAuditService.onProcessingUpdatingFile(order, "feature/version-2.0.0", "gradle.properties")
                autoVersioningAuditService.onPostProcessingStart(order, "feature/version-2.0.0")
                autoVersioningAuditService.onPostProcessingEnd(order, "feature/version-2.0.0")
                autoVersioningAuditService.onPRCreating(order, "feature/version-2.0.0")
                autoVersioningAuditService.onPRMerged(order, "feature/version-2.0.0", "#1", "uri:1")

                autoVersioningAuditQueryService.getByUUID(this, order.uuid).let { entry ->
                    assertEquals(order, entry.order)
                    assertEquals(AutoVersioningAuditState.PR_MERGED, entry.mostRecentState.state)
                    assertAudit(
                        entry,
                        audit(
                            AutoVersioningAuditState.PR_MERGED,
                            "branch" to "feature/version-2.0.0",
                            "prName" to "#1",
                            "prLink" to "uri:1"
                        ),
                        audit(AutoVersioningAuditState.PR_CREATING, "branch" to "feature/version-2.0.0"),
                        audit(AutoVersioningAuditState.POST_PROCESSING_END, "branch" to "feature/version-2.0.0"),
                        audit(AutoVersioningAuditState.POST_PROCESSING_START, "branch" to "feature/version-2.0.0"),
                        audit(
                            AutoVersioningAuditState.PROCESSING_UPDATING_FILE,
                            "branch" to "feature/version-2.0.0",
                            "path" to "gradle.properties"
                        ),
                        audit(AutoVersioningAuditState.PROCESSING_CREATING_BRANCH, "branch" to "feature/version-2.0.0"),
                        audit(AutoVersioningAuditState.PROCESSING_START),
                        audit(AutoVersioningAuditState.RECEIVED),
                        audit(AutoVersioningAuditState.CREATED)
                    )
                }
            }
        }
    }

    @Test
    fun `Error stack reduction`() {
        val source = project()
        project {
            branch {
                val order = createOrder(
                    sourceProject = source.name
                )
                val error = RuntimeException("test")
                val initialStack = ExceptionUtils.getStackTrace(error)

                autoVersioningAuditService.onQueuing(order, "routing")
                autoVersioningAuditService.onError(order, error)

                autoVersioningAuditQueryService.getByUUID(this, order.uuid).let { entry ->
                    assertEquals(order, entry.order)
                    assertEquals(AutoVersioningAuditState.ERROR, entry.mostRecentState.state)
                    assertNotNull(entry.mostRecentState.data["error"]) { stack ->
                        assertTrue(stack.length < initialStack.length)
                    }
                }
            }
        }
    }

    @Test
    fun `Errored auto versioning`() {
        val source = project()
        project {
            branch {
                val order = createOrder(
                    sourceProject = source.name
                )

                val error = RuntimeException("test")

                autoVersioningAuditService.onQueuing(order, "routing")
                autoVersioningAuditService.onReceived(order, "queue")
                autoVersioningAuditService.onProcessingStart(order)
                autoVersioningAuditService.onProcessingCreatingBranch(order, "feature/version-2.0.0")
                autoVersioningAuditService.onProcessingUpdatingFile(order, "feature/version-2.0.0", "gradle.properties")
                autoVersioningAuditService.onPRCreating(order, "feature/version-2.0.0")
                autoVersioningAuditService.onError(order, error)

                autoVersioningAuditQueryService.getByUUID(this, order.uuid).let { entry ->
                    assertEquals(order, entry.order)
                    assertEquals(AutoVersioningAuditState.ERROR, entry.mostRecentState.state)
                    assertAudit(
                        entry,
                        audit(
                            AutoVersioningAuditState.ERROR,
                            "error" to reducedStackTrace(error)
                        ),
                        audit(AutoVersioningAuditState.PR_CREATING, "branch" to "feature/version-2.0.0"),
                        audit(
                            AutoVersioningAuditState.PROCESSING_UPDATING_FILE,
                            "branch" to "feature/version-2.0.0",
                            "path" to "gradle.properties"
                        ),
                        audit(AutoVersioningAuditState.PROCESSING_CREATING_BRANCH, "branch" to "feature/version-2.0.0"),
                        audit(AutoVersioningAuditState.PROCESSING_START),
                        audit(AutoVersioningAuditState.RECEIVED),
                        audit(AutoVersioningAuditState.CREATED)
                    )
                }
            }
        }
    }

    @Test
    fun `Running states for a complete process`() {

        fun Branch.checkRunning(order: AutoVersioningOrder, expected: Boolean, after: (AutoVersioningOrder) -> Unit) {
            after(order)
            assertEquals(expected, autoVersioningAuditQueryService.getByUUID(this, order.uuid).running)
        }

        val source = project()
        project {
            branch {
                val order = createOrder(
                    sourceProject = source.name
                )
                checkRunning(order, true) { autoVersioningAuditService.onQueuing(order, "routing") }
                checkRunning(order, true) { autoVersioningAuditService.onReceived(order, "queue") }
                checkRunning(order, true) {
                    autoVersioningAuditService.onPostProcessingStart(
                        order,
                        "feature/version-2.0.0"
                    )
                }
                checkRunning(order, true) {
                    autoVersioningAuditService.onProcessingCreatingBranch(
                        order,
                        "feature/version-2.0.0"
                    )
                }
                checkRunning(order, true) {
                    autoVersioningAuditService.onProcessingUpdatingFile(
                        order,
                        "feature/version-2.0.0",
                        "gradle.properties"
                    )
                }
                checkRunning(order, true) { autoVersioningAuditService.onPRCreating(order, "feature/version-2.0.0") }
                checkRunning(order, false) {
                    autoVersioningAuditService.onPRMerged(
                        order,
                        "feature/version-2.0.0",
                        "#1",
                        "uri:1"
                    )
                }
            }
        }
    }

    @Test
    fun `Running states for an aborted process`() {

        fun Branch.checkRunning(order: AutoVersioningOrder, expected: Boolean, after: (AutoVersioningOrder) -> Unit) {
            after(order)
            assertEquals(expected, autoVersioningAuditQueryService.getByUUID(this, order.uuid).running)
        }

        val source = project()
        project {
            branch {
                val order = createOrder(
                    sourceProject = source.name
                )
                checkRunning(order, true) { autoVersioningAuditService.onQueuing(order, "routing") }
                checkRunning(order, true) { autoVersioningAuditService.onReceived(order, "queue") }
                checkRunning(order, true) {
                    autoVersioningAuditService.onPostProcessingStart(
                        order,
                        "feature/version-2.0.0"
                    )
                }
                checkRunning(order, true) {
                    autoVersioningAuditService.onProcessingCreatingBranch(
                        order,
                        "feature/version-2.0.0"
                    )
                }
                checkRunning(order, true) {
                    autoVersioningAuditService.onProcessingUpdatingFile(
                        order,
                        "feature/version-2.0.0",
                        "gradle.properties"
                    )
                }
                checkRunning(order, true) { autoVersioningAuditService.onPRCreating(order, "feature/version-2.0.0") }
                checkRunning(order, false) { autoVersioningAuditService.onProcessingAborted(order, "Timeout") }
            }
        }
    }

    @Test
    fun `Running states for an errored process`() {

        fun Branch.checkRunning(order: AutoVersioningOrder, expected: Boolean, after: (AutoVersioningOrder) -> Unit) {
            after(order)
            assertEquals(expected, autoVersioningAuditQueryService.getByUUID(this, order.uuid).running)
        }

        val source = project()
        project {
            branch {
                val order = createOrder(
                    sourceProject = source.name
                )
                checkRunning(order, true) { autoVersioningAuditService.onQueuing(order, "routing") }
                checkRunning(order, true) { autoVersioningAuditService.onReceived(order, "queue") }
                checkRunning(order, true) {
                    autoVersioningAuditService.onPostProcessingStart(
                        order,
                        "feature/version-2.0.0"
                    )
                }
                checkRunning(order, true) {
                    autoVersioningAuditService.onProcessingCreatingBranch(
                        order,
                        "feature/version-2.0.0"
                    )
                }
                checkRunning(order, true) {
                    autoVersioningAuditService.onProcessingUpdatingFile(
                        order,
                        "feature/version-2.0.0",
                        "gradle.properties"
                    )
                }
                checkRunning(order, true) { autoVersioningAuditService.onPRCreating(order, "feature/version-2.0.0") }
                checkRunning(order, false) { autoVersioningAuditService.onError(order, IllegalStateException("error")) }
            }
        }
    }

}