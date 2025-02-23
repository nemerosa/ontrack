package net.nemerosa.ontrack.kdsl.acceptance.tests.workflows

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.workflows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ACCDSLWorkflowJSONSchema : AbstractACCDSLTestSupport() {

    @Test
    fun `Downloading the JSON schema for workflows`() {
        val schema = ontrack.workflows.downloadJsonSchema()
        assertEquals(
            "#/\$defs/workflow",
            schema.path("\$ref").asText()
        )
    }

}