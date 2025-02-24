package net.nemerosa.ontrack.boot.schema.json

import net.nemerosa.ontrack.extension.casc.schema.json.CascJsonSchemaService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

/**
 * Testing the generation of the Casc JSON schema at the highest level.
 */
class CascJsonSchemaIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var cascJsonSchemaService: CascJsonSchemaService

    @Test
    fun `Casc schema generation`() {
        val schema = cascJsonSchemaService.createCascJsonSchema()
        // Checking that defs are correctly generated, at least for some samples
        val defs = schema.path("${'$'}defs")
        val expectedDefs = listOf(
            "casc",
            "slot-admission-rule-branchPattern",
            "slot-admission-rule-environment",
            "slot-admission-rule-manual",
            "slot-admission-rule-promotion",
            "workflow-node-executor-auto-versioning",
            "workflow-node-executor-mock",
            "workflow-node-executor-notification",
            "workflow-node-executor-pause",
            "workflow-node-executor-slot-pipeline-creation",
            "workflow-node-executor-slot-pipeline-deployed",
            "workflow-node-executor-slot-pipeline-deploying",
        )
        for (expectedDef in expectedDefs) {
            assertTrue(defs.has(expectedDef), "Expecting def $expectedDef")
        }
    }

}