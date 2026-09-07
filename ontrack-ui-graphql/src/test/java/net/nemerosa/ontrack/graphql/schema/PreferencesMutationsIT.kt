package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.getBooleanField
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.model.preferences.PreferencesService
import net.nemerosa.ontrack.model.preferences.ThemeMode
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class PreferencesMutationsIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var preferencesService: PreferencesService

    @Test
    fun `Getting the default preferences`() {
        asUser {
            val preferences = getPreferences()
            assertEquals(false, preferences.branchViewVsNames)
            assertEquals(false, preferences.branchViewVsGroups)
            assertEquals(ThemeMode.SYSTEM, preferences.themeMode)
            // No view chosen yet: the page falls back to the classic one it has always shown
            assertEquals(null, preferences.selectedChangeLogViewKey)
            assertEquals("markdown", preferences.changeLogSemanticFormat)
            assertEquals(true, preferences.changeLogSemanticEmojis)
            assertEquals(true, preferences.changeLogSemanticIssues)
            assertEquals(false, preferences.changeLogSemanticCommits)
        }
    }

    /**
     * The change log view and its options are stored the same way the branch content view is, so
     * that "my change log always opens as Jira with emojis" follows a user between machines —
     * see `docs/adr/0008-change-log-views.md`.
     */
    @Test
    fun `Setting the change log view and its options`() {
        asUser {
            run(
                """
                mutation {
                    setPreferences(input: {
                        selectedChangeLogViewKey: "semantic",
                        changeLogSemanticFormat: "jira",
                        changeLogSemanticEmojis: false,
                        changeLogSemanticIssues: false,
                        changeLogSemanticCommits: true,
                    }) {
                        preferences {
                            selectedChangeLogViewKey
                            changeLogSemanticFormat
                            changeLogSemanticEmojis
                            changeLogSemanticIssues
                            changeLogSemanticCommits
                        }
                    }
                }
            """
            ) { data ->
                val preferences = data["setPreferences"]["preferences"]
                assertEquals("semantic", preferences.getTextField("selectedChangeLogViewKey"))
                assertEquals("jira", preferences.getTextField("changeLogSemanticFormat"))
                assertEquals(false, preferences.getBooleanField("changeLogSemanticEmojis"))
                assertEquals(false, preferences.getBooleanField("changeLogSemanticIssues"))
                assertEquals(true, preferences.getBooleanField("changeLogSemanticCommits"))
            }
            val stored = getPreferences()
            assertEquals("semantic", stored.selectedChangeLogViewKey)
            assertEquals("jira", stored.changeLogSemanticFormat)
            assertEquals(false, stored.changeLogSemanticEmojis)
            assertEquals(false, stored.changeLogSemanticIssues)
            assertEquals(true, stored.changeLogSemanticCommits)
        }
    }

    /**
     * Every option is written on its own, as the page does when one control is touched, so
     * setting one must not reset the others to their defaults.
     */
    @Test
    fun `Setting one change log option leaves the others alone`() {
        asUser {
            run("""mutation { setPreferences(input: { changeLogSemanticFormat: "jira" }) { preferences { changeLogSemanticFormat } } }""")
            run("""mutation { setPreferences(input: { changeLogSemanticEmojis: false }) { preferences { changeLogSemanticEmojis } } }""")
            val stored = getPreferences()
            assertEquals("jira", stored.changeLogSemanticFormat)
            assertEquals(false, stored.changeLogSemanticEmojis)
        }
    }

    @Test
    fun `Setting the theme mode`() {
        asUser {
            run(
                """
                mutation {
                    setPreferences(input: {
                        themeMode: DARK,
                    }) {
                        preferences {
                            themeMode
                        }
                    }
                }
            """
            ) { data ->
                val preferences = data["setPreferences"]["preferences"]
                assertEquals("DARK", preferences.getTextField("themeMode"))
                assertEquals(ThemeMode.DARK, getPreferences().themeMode)
            }
        }
    }

    @Test
    fun `Setting another preference leaves the theme mode alone`() {
        asUser {
            run(
                """
                mutation {
                    setPreferences(input: {
                        themeMode: LIGHT,
                    }) {
                        preferences {
                            themeMode
                        }
                    }
                }
            """
            )
            run(
                """
                mutation {
                    setPreferences(input: {
                        branchViewVsNames: true,
                    }) {
                        preferences {
                            themeMode
                        }
                    }
                }
            """
            ) { data ->
                val preferences = data["setPreferences"]["preferences"]
                assertEquals("LIGHT", preferences.getTextField("themeMode"))
                assertEquals(ThemeMode.LIGHT, getPreferences().themeMode)
            }
        }
    }

    @Test
    fun `Setting all the preferences`() {
        asUser {
            run(
                """
                mutation {
                    setPreferences(input: {
                        branchViewVsNames: true,
                        branchViewVsGroups: true,
                    }) {
                        preferences {
                            branchViewVsNames
                            branchViewVsGroups
                        }
                    }
                }
            """
            ) { data ->
                val preferences = data["setPreferences"]["preferences"]
                assertEquals(true, preferences.getBooleanField("branchViewVsNames"))
                assertEquals(true, preferences.getBooleanField("branchViewVsGroups"))
                getPreferences().let {
                    assertEquals(true, it.branchViewVsNames)
                    assertEquals(true, it.branchViewVsGroups)
                }
            }
        }
    }

    @Test
    fun `Setting only one preference`() {
        asUser {
            run(
                """
                mutation {
                    setPreferences(input: {
                        branchViewVsNames: true,
                    }) {
                        preferences {
                            branchViewVsNames
                            branchViewVsGroups
                        }
                    }
                }
            """
            ) { data ->
                val preferences = data["setPreferences"]["preferences"]
                assertEquals(true, preferences.getBooleanField("branchViewVsNames"))
                assertEquals(false, preferences.getBooleanField("branchViewVsGroups"))
                getPreferences().let {
                    assertEquals(true, it.branchViewVsNames)
                    assertEquals(false, it.branchViewVsGroups)
                }
            }
        }
    }

    private fun getPreferences() = preferencesService.getPreferences(
        securityService.currentUser?.account
            ?: error("Authentication is required")
    )

}