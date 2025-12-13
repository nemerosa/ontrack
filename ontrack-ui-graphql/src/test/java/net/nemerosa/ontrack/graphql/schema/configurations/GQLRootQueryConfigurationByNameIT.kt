package net.nemerosa.ontrack.graphql.schema.configurations

import net.nemerosa.ontrack.extension.api.support.TestConfiguration
import net.nemerosa.ontrack.extension.api.support.TestConfigurationService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GQLRootQueryConfigurationByNameIT(
    @Autowired
    private val testConfigurationService: TestConfigurationService,
) : AbstractQLKTITSupport() {

    @Test
    fun `Configuration by name`() {
        asAdmin {

            val config = testConfigurationService.newConfiguration(
                configuration = TestConfiguration(
                    name = uid("config-"),
                    user = "test",
                    password = "test",
                )
            )

            run(
                """
                    {
                        configurationByName(type: "test", name: "${config.name}") {
                            name
                            data
                            extra
                        }
                    }
                """
            ) { data ->
                val result = data.path("configurationByName")
                assertEquals(config.name, result.path("name").asText())
                assertEquals(config.user, result.path("data").path("user").asText())
                assertEquals("", result.path("data").path("password").asText())
            }
        }
    }

}