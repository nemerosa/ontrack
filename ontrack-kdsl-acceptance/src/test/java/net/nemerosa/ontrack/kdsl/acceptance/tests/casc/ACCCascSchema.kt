package net.nemerosa.ontrack.kdsl.acceptance.tests.casc

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.spec.extension.casc.casc
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ACCCascSchema: AbstractACCDSLTestSupport() {

    @Test
    fun `Downloading the JSON schema for Casc`() {
        val schema = ontrack.casc.downloadJsonSchema()
        assertEquals(
            "#/\$defs/casc",
            schema.path("\$ref").asText()
        )
    }

}