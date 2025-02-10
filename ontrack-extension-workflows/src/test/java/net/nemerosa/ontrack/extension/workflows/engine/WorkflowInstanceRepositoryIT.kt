package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowFixtures
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowParser
import net.nemerosa.ontrack.extension.workflows.repository.WorkflowInstanceRepository
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

class WorkflowInstanceRepositoryIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var workflowInstanceRepository: WorkflowInstanceRepository

    @Autowired
    private lateinit var serializableEventService: SerializableEventService

    @Test
    fun `Saving and retrieving one workflow instance from a database`() {
        val instance = createInstance(
            workflow = WorkflowParser.parseYamlWorkflow(WorkflowFixtures.simpleLinearWorkflowYaml),
            event = serializableEventService.dehydrate(
                MockEventType.mockEvent("Some text")
            ),
            triggerData = TODO(),
        )
        workflowInstanceRepository.createInstance(instance)

        assertNotNull(workflowInstanceRepository.findWorkflowInstance(instance.id)) { saved ->
            assertEquals(
                instance.truncateTimestamp(),
                saved.truncateTimestamp(),
            )
        }
    }

    @Test
    fun `Filtering workflows by name`() {
        val instances = (1..2).map {
            createInstance(
                workflow = WorkflowParser.parseYamlWorkflow(WorkflowFixtures.simpleLinearWorkflowYaml)
                    .rename { uid("w-") },
                event = serializableEventService.dehydrate(
                    MockEventType.mockEvent("Some text")
                ),
                triggerData = TODO(),
            ).apply {
                workflowInstanceRepository.createInstance(this)
            }
        }

        val found = workflowInstanceRepository.findInstances(
            WorkflowInstanceFilter(name = instances[1].workflow.name)
        ).pageItems

        assertEquals(1, found.size)
        assertEquals(instances[1].id, found.first().id)
    }

    @Test
    fun `Cleaning of workflow instances`() {
        asAdmin {
            // Removing all previous instances for the test
            workflowInstanceRepository.clearAll()
            // Reference time
            val now = Time.now
            // Saving an instance two days ago
            val old = WorkflowInstanceFixtures.simpleLinear(
                timestamp = now - Duration.ofDays(2)
            )
            workflowInstanceRepository.createInstance(old)
            // Saving an instance now
            val recent = WorkflowInstanceFixtures.simpleLinear(
                timestamp = now
            )
            workflowInstanceRepository.createInstance(recent)
            // Launching the cleanup
            workflowInstanceRepository.cleanup(now - Duration.ofDays(1))
            // Only the most recent instance is present
            assertNull(workflowInstanceRepository.findWorkflowInstance(old.id), "Old instance is gone")
            assertNotNull(workflowInstanceRepository.findWorkflowInstance(recent.id), "Recent instance has been kept")
        }
    }
}
