package net.nemerosa.ontrack.boot.schema.json

import net.nemerosa.ontrack.boot.Application
import net.nemerosa.ontrack.extension.config.schema.CIConfigJsonSchemaProvider
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File

@SpringBootTest(classes = [Application::class])
class CIConfigJsonSchemaProviderIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var provider: CIConfigJsonSchemaProvider

    @Test
    @AsAdminTest
    fun `Creating the Casc JSON schema`() {
        val schema = provider.createJsonSchema()
        val schemaJson = schema.toPrettyString()
        println(schemaJson)

        val file = File("yontrack-ci-config-schema.json")
        file.writeText(schemaJson)
    }

}