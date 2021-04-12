package net.nemerosa.ontrack.extension.casc.schema

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull

class CascSchemaIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var cascSchemaService: CascSchemaService

    @Test
    fun `Overall schema`() {
        val schema = cascSchemaService.schema
        assertIs<CascObject>(schema) { root ->
            val ontrack = root.fields.find { it.name == "ontrack" }
            assertNotNull(ontrack) {
                assertIs<CascObject>(it.type)
            }
        }
    }

}