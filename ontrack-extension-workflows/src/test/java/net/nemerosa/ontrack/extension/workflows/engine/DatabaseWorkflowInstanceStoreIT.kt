package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowFixtures
import net.nemerosa.ontrack.extension.workflows.mgt.WorkflowSettings
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DatabaseWorkflowInstanceStoreIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var databaseWorkflowInstanceStore: DatabaseWorkflowInstanceStore

    @Test
    fun `Saving and retrieving one workflow instance from a database`() {
        val instance = createInstance(
            workflow = WorkflowFixtures.simpleLinearWorkflow(),
            context = WorkflowContext(
                key = "mock",
                value = mapOf("text" to "Some text").asJson()
            )
        )
        databaseWorkflowInstanceStore.store(instance)

        assertNotNull(databaseWorkflowInstanceStore.findById(instance.id)) { saved ->
            assertEquals(instance.asJson(), saved.asJson())
        }
    }

    @Test
    fun `Cleaning of workflow instances`() {
        asAdmin {
            withCleanSettings<WorkflowSettings> {
                // Retention period to one day
                settingsManagerService.saveSettings(
                    WorkflowSettings(
                        retentionDuration = Duration.ofDays(1).toMillis(),
                    )
                )
                // Removing all previous instances for the test
                databaseWorkflowInstanceStore.clearAll()
                // Reference time
                val now = Time.now
                // Saving an instance two days ago
                val old = WorkflowInstanceFixtures.simpleLinear(
                    timestamp = now - Duration.ofDays(2)
                )
                databaseWorkflowInstanceStore.store(old)
                // Saving an instance now
                val recent = WorkflowInstanceFixtures.simpleLinear(
                    timestamp = now
                )
                databaseWorkflowInstanceStore.store(recent)
                // Launching the cleanup
                databaseWorkflowInstanceStore.cleanup()
                // Only the most recent instance is present
                assertNull(databaseWorkflowInstanceStore.findById(old.id), "Old instance is gone")
                assertNotNull(databaseWorkflowInstanceStore.findById(recent.id), "Recent instance has been kept")
            }
        }
    }

}