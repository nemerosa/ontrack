package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.getBooleanField
import net.nemerosa.ontrack.model.preferences.PreferencesService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class PreferencesMutationsIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var preferencesService: PreferencesService

    @Test
    fun `Getting the default preferences`() {
        asUser {
            val preferences = getPreferences()
            assertEquals(true, preferences.branchViewLegacy)
            assertEquals(true, preferences.branchViewVsNames)
            assertEquals(true, preferences.branchViewVsGroups)
        }
    }

    @Test
    fun `Setting all the preferences`() {
        asUser {
            run("""
                mutation {
                    setPreferences(input: {
                        branchViewLegacy: false,
                        branchViewVsNames: false,
                        branchViewVsGroups: false,
                    }) {
                        preferences {
                            branchViewLegacy
                            branchViewVsNames
                            branchViewVsGroups
                        }
                    }
                }
            """) { data ->
                val preferences = data["setPreferences"]["preferences"]
                assertEquals(false, preferences.getBooleanField("branchViewLegacy"))
                assertEquals(false, preferences.getBooleanField("branchViewVsNames"))
                assertEquals(false, preferences.getBooleanField("branchViewVsGroups"))
                getPreferences().let {
                    assertEquals(false, it.branchViewLegacy)
                    assertEquals(false, it.branchViewVsNames)
                    assertEquals(false, it.branchViewVsGroups)
                }
            }
        }
    }

    @Test
    fun `Setting only one preference`() {
        asUser {
            run("""
                mutation {
                    setPreferences(input: {
                        branchViewLegacy: false,
                    }) {
                        preferences {
                            branchViewLegacy
                            branchViewVsNames
                            branchViewVsGroups
                        }
                    }
                }
            """) { data ->
                val preferences = data["setPreferences"]["preferences"]
                assertEquals(false, preferences.getBooleanField("branchViewLegacy"))
                assertEquals(true, preferences.getBooleanField("branchViewVsNames"))
                assertEquals(true, preferences.getBooleanField("branchViewVsGroups"))
                getPreferences().let {
                    assertEquals(false, it.branchViewLegacy)
                    assertEquals(true, it.branchViewVsNames)
                    assertEquals(true, it.branchViewVsGroups)
                }
            }
        }
    }

    private fun getPreferences() = preferencesService.getPreferences(securityService.currentAccount?.account
        ?: error("Authentication is required"))

}