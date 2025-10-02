package net.nemerosa.ontrack.extension.jira

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class JIRAConfigurationServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var jiraConfigurationService: JIRAConfigurationService

    @Test
    fun update_name_check() {
        withDisabledConfigurationTest {
            asAdmin {
                assertFailsWith<IllegalStateException> {
                    val name = uid("j-")
                    jiraConfigurationService.updateConfiguration(
                        uid("j-"),
                        JIRAConfiguration(
                            name = name,
                            url ="https://host",
                            user = "user",
                            password = "",
                            include = emptyList(),
                            exclude = emptyList())
                    )
                }
            }
        }
    }

    @Test
    fun update_blank_password() {
        withDisabledConfigurationTest {
            asAdmin {
                val config = jiraConfig()
                jiraConfigurationService.newConfiguration(config)

                jiraConfigurationService.updateConfiguration(
                    config.name,
                    config.withPassword("")
                )

                assertNotNull(jiraConfigurationService.findConfiguration(config.name)) {
                    assertEquals("secret", it.password)
                }
            }
        }
    }

    @Test
    fun update_blank_password_for_different_user() {
        withDisabledConfigurationTest {
            asAdmin {
                val config = jiraConfig()
                jiraConfigurationService.newConfiguration(config)

                jiraConfigurationService.updateConfiguration(
                    config.name,
                    config.run {
                        JIRAConfiguration(
                            name = name,
                            url = url,
                            user = "user1",
                            password = "",
                            include = include,
                            exclude = exclude
                        )
                    }
                )

                assertNotNull(jiraConfigurationService.findConfiguration(config.name)) {
                    assertEquals("user1", it.user)
                    assertEquals("", it.password)
                }
            }
        }
    }

    @Test
    fun update_new_password() {
        withDisabledConfigurationTest {
            asAdmin {
                val config = jiraConfig()
                jiraConfigurationService.newConfiguration(config)

                jiraConfigurationService.updateConfiguration(
                    config.name,
                    config.withPassword("new-secret")
                )

                assertNotNull(jiraConfigurationService.findConfiguration(config.name)) {
                    assertEquals("new-secret", it.password)
                }
            }
        }
    }

    private fun jiraConfig() =
        JIRAConfiguration(
            name = uid("j-"),
            url = "https://host",
            user = "user",
            password = "secret",
            include = emptyList(),
            exclude = emptyList()
        )
}
