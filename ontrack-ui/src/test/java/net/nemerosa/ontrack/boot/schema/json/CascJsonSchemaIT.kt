package net.nemerosa.ontrack.boot.schema.json

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertTrue

/**
 * Testing the generation of the Casc JSON schema at the highest level.
 */
class CascJsonSchemaIT : AbstractCascTestSupport() {

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
        // Writing the JSON schema locally
        File("ontrack-casc-schema.json").writeText(
            schema.toPrettyString()
        )
    }

    @Test
    fun `Casc schema validation`() {
        val yaml = TestUtils.resourceString("/schema/json/casc-environments.yaml")
        assertValidYaml(yaml)
    }

}