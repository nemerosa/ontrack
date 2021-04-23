package net.nemerosa.ontrack.extension.casc.graphql

import net.nemerosa.ontrack.extension.casc.CascConfigurationProperties
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.settings.HomePageSettings
import net.nemerosa.ontrack.test.assertJsonNotNull
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Test
    fun `YAML rendering`() {
        asAdmin {
            withSettings<HomePageSettings> {
                settingsManagerService.saveSettings(
                    HomePageSettings(
                        maxBranches = 2,
                        maxProjects = 200,
                    )
                )
                run("""
                    {
                        casc {
                            yaml
                        }
                    }
                """).let { data ->
                    val yaml = data.path("casc").path("yaml").asText()
                    assertTrue("maxBranches: 2" in yaml)
                    assertTrue("maxProjects: 200" in yaml)
                }
            }
        }
    }

    @Test
    fun `Rendering as JSON`() {
        asAdmin {
            withSettings<HomePageSettings> {
                settingsManagerService.saveSettings(
                    HomePageSettings(
                        maxBranches = 2,
                        maxProjects = 200,
                    )
                )
                run("""
                    {
                        casc {
                            json
                        }
                    }
                """).let { data ->
                    val json = data.path("casc").path("json")
                    assertEquals(2,
                        json.path("ontrack").path("config").path("settings").path("home-page").path("maxBranches").asInt())
                    assertEquals(200,
                        json.path("ontrack").path("config").path("settings").path("home-page").path("maxProjects").asInt())
                }
            }
        }
    }

    @Test
    fun `Reloading the configuration as code`() {
        asAdmin {
            withSettings<HomePageSettings> {
                // Configuration
                cascConfigurationProperties.locations = listOf(
                    "classpath:casc/settings-home-page.yaml",
                )
                // Initial values
                settingsManagerService.saveSettings(
                    HomePageSettings(
                        maxBranches = 2,
                        maxProjects = 200,
                    )
                )
                // Reloading
                val data = run("""
                    mutation {
                        reloadCasc {
                            errors {
                                message
                            }
                        }
                    }
                """)
                // Checks there are no error
                assertNoUserError(data, "reloadCasc")
                // Checks the values have been updated
                val settings = cachedSettingsService.getCachedSettings(HomePageSettings::class.java)
                assertEquals(10, settings.maxBranches)
                assertEquals(100, settings.maxProjects)
            }
        }
    }
}