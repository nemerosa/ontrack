package net.nemerosa.ontrack.extension.casc.schema

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull

class CascSchemaIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var cascSchemaService: CascSchemaService

    @Test
    fun `Overall schema`() {
        val schema = asAdmin {
            cascSchemaService.schema
        }
        assertIs<CascObject>(schema) { root ->
            val ontrack = root.fields.find { it.name == "ontrack" }
            assertNotNull(ontrack) { fOntrack ->
                assertIs<CascObject>(fOntrack.type) { oOntrack ->
                    val admin = oOntrack.fields.find { it.name == "admin" }
                    assertNotNull(admin, "admin field is found under ontrack") { fAdmin ->
                        assertIs<CascObject>(fAdmin.type) { oAdmin ->
                            val groups = oAdmin.fields.find { it.name == "groups" }
                            assertNotNull(groups, "groups field is found under admin") { fGroups ->
                                assertIs<CascArray>(fGroups.type)
                            }
                        }
                    }
                }
            }
        }
    }

}