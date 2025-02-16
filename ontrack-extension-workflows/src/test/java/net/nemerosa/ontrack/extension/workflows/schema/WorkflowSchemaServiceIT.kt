package net.nemerosa.ontrack.extension.workflows.schema

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class WorkflowSchemaServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var workflowSchemaService: WorkflowSchemaService

    @Test
    fun `Creating the Casc JSON schema`() {
        val schema = asAdmin {
            workflowSchemaService.createJsonSchema()
        }
        println(schema.toPrettyString())
    }

}