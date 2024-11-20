package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowFixtures
import net.nemerosa.ontrack.extension.workflows.mgt.WorkflowSettings
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.MockEventType
import net.nemerosa.ontrack.model.events.SerializableEventService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DatabaseWorkflowInstanceStoreIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var databaseWorkflowInstanceStore: DatabaseWorkflowInstanceStore

    @Autowired
    private lateinit var serializableEventService: SerializableEventService

    @Test
    fun `Saving and retrieving one workflow instance from a database`() {
        val instance = createInstance(
            workflow = WorkflowFixtures.simpleLinearWorkflow(),
            event = serializableEventService.dehydrate(
                MockEventType.mockEvent("Some text")
            ),
        )
        databaseWorkflowInstanceStore.create(instance)

        assertNotNull(databaseWorkflowInstanceStore.findById(instance.id)) { saved ->
            assertEquals(instance.id, saved.id)
            assertEquals(instance.status, saved.status)
            assertEquals(instance.event, saved.event)
            assertEquals(instance.workflow, saved.workflow)
            assertEquals(instance.nodesExecutions, saved.nodesExecutions)
        }
    }

    @Test
    fun `Filtering workflows by name`() {
        val instances = (1..2).map {
            createInstance(
                workflow = WorkflowFixtures.simpleLinearWorkflow(
                    name = uid("w-")
                ),
                event = serializableEventService.dehydrate(
                    MockEventType.mockEvent("Some text")
                ),
            ).apply {
                databaseWorkflowInstanceStore.create(this)
            }
        }

        val found = databaseWorkflowInstanceStore.findByFilter(
            WorkflowInstanceFilter(name = instances[1].workflow.name)
        ).pageItems

        assertEquals(1, found.size)
        assertEquals(instances[1].id, found.first().id)
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
                databaseWorkflowInstanceStore.create(old)
                // Saving an instance now
                val recent = WorkflowInstanceFixtures.simpleLinear(
                    timestamp = now
                )
                databaseWorkflowInstanceStore.create(recent)
                // Launching the cleanup
                databaseWorkflowInstanceStore.cleanup()
                // Only the most recent instance is present
                assertNull(databaseWorkflowInstanceStore.findById(old.id), "Old instance is gone")
                assertNotNull(databaseWorkflowInstanceStore.findById(recent.id), "Recent instance has been kept")
            }
        }
    }

}