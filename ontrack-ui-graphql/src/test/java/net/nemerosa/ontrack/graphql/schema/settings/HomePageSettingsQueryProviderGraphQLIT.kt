package net.nemerosa.ontrack.graphql.schema.settings

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.model.settings.HomePageSettings
import org.junit.Test
import kotlin.test.assertEquals

class HomePageSettingsQueryProviderGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Settings are accessible in admin mode`() {
        doTestSettings { code ->
            asAdmin {
                code()
            }
        }
    }

    @Test
    fun `Settings are accessible in read-only mode`() {
        doTestSettings { code ->
            withNoGrantViewToAll {
                asAccountWithGlobalRole(Roles.GLOBAL_READ_ONLY) {
                    code()
                }
            }
        }
    }

    private fun doTestSettings(
        wrapper: (() -> Unit) -> Unit
    ) {
        asAdmin {
            withSettings<HomePageSettings> {
                settingsManagerService.saveSettings(
                    HomePageSettings(
                        maxBranches = 20,
                        maxProjects = 100
                    )
                )
                wrapper {
                    run("""{
                        settings {
                            homePage {
                                maxBranches
                                maxProjects
                            }
                        }
                    }""").let { data ->
                        val homePage = data.path("settings").path("homePage")
                        assertEquals(20, homePage.path("maxBranches").asInt())
                        assertEquals(100, homePage.path("maxProjects").asInt())
                    }
                }
            }
        }
    }

}