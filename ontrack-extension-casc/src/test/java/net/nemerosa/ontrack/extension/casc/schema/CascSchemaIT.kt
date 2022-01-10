package net.nemerosa.ontrack.extension.casc.schema

import net.nemerosa.ontrack.it.AbstractDSLTestJUnit4Support
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull

class CascSchemaIT : AbstractDSLTestJUnit4Support() {

    @Autowired
    private lateinit var cascSchemaService: CascSchemaService

    @Test
    fun `Overall schema`() {
        val schema = asAdmin {
            cascSchemaService.schema
        }
        assertIs<CascObject>(schema) { root ->
            val ontrack = root.fields.find { it.name == "ontrack" }
            assertNotNull(ontrack) {
                assertIs<CascObject>(it.type)
            }
        }
    }

}