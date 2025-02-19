package net.nemerosa.ontrack.extension.tfc.config

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.model.json.schema.JsonArrayType
import net.nemerosa.ontrack.model.json.schema.JsonObjectType
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TFCConfigurationContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var tfcConfigurationService: TFCConfigurationService

    @Autowired
    private lateinit var tfcConfigurationCasc: TFCConfigurationCasc

    @Test
    fun `Defining a TFC configuration`() {
        val name = TestUtils.uid("TFC_")
        withDisabledConfigurationTest {
            casc(
                """
                    ontrack:
                        config:
                            tfc:
                                - name: $name
                                  url: "https://app.terraform.io"
                                  token: my-secret-token
                """.trimIndent()
            )
        }
        // Checks the TFC configuration has been registered
        asAdmin {
            val configurations = tfcConfigurationService.configurations
            assertEquals(1, configurations.size, "Only one configuration is defined")
            val configuration = configurations.first()
            assertEquals(name, configuration.name)
            assertEquals("https://app.terraform.io", configuration.url)
            assertEquals("my-secret-token", configuration.token)
        }
    }

    @Test
    fun `Generated JSON schema must not include the descriptor`() {
        val type = tfcConfigurationCasc.jsonType
        assertNull(
            ((type as JsonArrayType).items as JsonObjectType).properties["descriptor"]
        )
    }

}