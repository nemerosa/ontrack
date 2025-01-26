package net.nemerosa.ontrack.extension.workflows.repository

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.support.StorageService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WorkflowInstanceRepositoryMigrationIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var workflowInstanceRepositoryMigration: WorkflowInstanceRepositoryMigration

    @Autowired
    private lateinit var storageService: StorageService

    @Test
    fun `Cleanup of legacy records`() {
        // Creating a legacy record for a workflow
        val legacyWorkflowRecordId = UUID.randomUUID().toString()
        storageService.storeJson(
            store = "net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance",
            key = legacyWorkflowRecordId,
            node = mapOf(
                "id" to legacyWorkflowRecordId,
            ).asJson()
        )
        // Creating a notification record NOT for a workflow
        val notificationRecordId = UUID.randomUUID().toString()
        storageService.storeJson(
            store = "net.nemerosa.ontrack.extension.notifications.recording.NotificationRecord",
            key = notificationRecordId,
            node = mapOf(
                "id" to notificationRecordId,
                "channel" to "other"
            ).asJson()
        )
        // Creating a legacy record for a workflow notification
        val legacyWorkflowNotificationRecord = UUID.randomUUID().toString()
        storageService.storeJson(
            store = "net.nemerosa.ontrack.extension.notifications.recording.NotificationRecord",
            key = legacyWorkflowNotificationRecord,
            node = mapOf(
                "id" to legacyWorkflowNotificationRecord,
                "channel" to "workflow",
                "channelConfig" to mapOf(
                    "workflow" to mapOf(
                        "name" to "Legacy workflow record"
                    )
                )
            ).asJson()
        )
        // Creating a modern record for a workflow notification (with pauseMs = 0)
        val modernWorkflowNotificationRecord = UUID.randomUUID().toString()
        storageService.storeJson(
            store = "net.nemerosa.ontrack.extension.notifications.recording.NotificationRecord",
            key = modernWorkflowNotificationRecord,
            node = mapOf(
                "id" to modernWorkflowNotificationRecord,
                "channel" to "workflow",
                "channelConfig" to mapOf(
                    "pauseMs" to 0,
                    "workflow" to mapOf(
                        "name" to "New workflow record"
                    )
                )
            ).asJson()
        )
        // Launching the migration
        // Twice to check upon idempotency
        repeat(2) {
            workflowInstanceRepositoryMigration.start()
            // Checks the workflow records are empty
            assertEquals(
                0,
                storageService.count("net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance")
            )
            // Checks the non-workflow notification is still there
            assertTrue(
                storageService.exists(
                    "net.nemerosa.ontrack.extension.notifications.recording.NotificationRecord",
                    notificationRecordId
                ),
                "The non-workflow notification is still there"
            )
            // Checks the legacy workflow notification is gone
            assertFalse(
                storageService.exists(
                    "net.nemerosa.ontrack.extension.notifications.recording.NotificationRecord",
                    legacyWorkflowNotificationRecord
                ),
                "The legacy workflow notification is gone"
            )
            // Checks the modern workflow notification is still there
            assertTrue(
                storageService.exists(
                    "net.nemerosa.ontrack.extension.notifications.recording.NotificationRecord",
                    modernWorkflowNotificationRecord
                ),
                "The modern workflow notification is still there"
            )
        }
    }

}