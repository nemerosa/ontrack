package net.nemerosa.ontrack.extension.config.schema

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CIConfigJsonSchemaProviderIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var provider: CIConfigJsonSchemaProvider

    @Test
    @AsAdminTest
    fun `Creating the Casc JSON schema`() {
        val schema = provider.createJsonSchema()
        println(schema.toPrettyString())
    }

}