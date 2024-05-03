package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.extension.workflows.definition.WorkflowFixtures
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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

}