package net.nemerosa.ontrack.extension.casc.graphql

import net.nemerosa.ontrack.extension.casc.CascConfigurationProperties
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.test.assertJsonNotNull
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class CascGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var cascConfigurationProperties: CascConfigurationProperties

    @Test
    fun `Accessing the Casc schema`() {
        asAdmin {
            run("""
                {
                    casc {
                        schema
                    }
                }
            """).let { data ->
                val schema = data.path("casc").path("schema")
                assertJsonNotNull(schema)
            }
        }
    }

    @Test
    fun `Accessing the Casc resources`() {
        cascConfigurationProperties.apply {
            locations = listOf(
                "classpath:casc/settings-security.yaml",
                "classpath:casc/settings-home-page.yaml",
            )
        }
        asAdmin {
            run("""
                {
                    casc {
                        locations
                    }
                }
            """).let { data ->
                val locations = data.path("casc").path("locations").map { it.asText() }
                assertEquals(
                    listOf(
                        "classpath:casc/settings-security.yaml",
                        "classpath:casc/settings-home-page.yaml",
                    ),
                    locations
                )
            }
        }
    }
}