package net.nemerosa.ontrack.extension.workflows.graphql

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.getRequiredTextField
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GQLRootQueryWorkflowNodeExecutorsIT : AbstractQLKTITSupport() {

    @Test
    fun `Getting the list of executors`() {
        asAdmin {
            run(
                """
                {
                    workflowNodeExecutors {
                        id
                        displayName
                    }
                }
            """
            ) { data ->
                assertNotNull(
                    data.path("workflowNodeExecutors").find { it.getRequiredTextField("id") == "notification" }
                ) {
                    assertEquals("Notification", it.getRequiredTextField("displayName"))
                }
            }
        }
    }

}