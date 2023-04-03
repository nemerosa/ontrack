package net.nemerosa.ontrack.extension.jira.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationService
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class JIRAConfigurationCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var jiraConfigurationService: JIRAConfigurationService

    @Test
    fun `Defining a JIRA configuration`() {
        val name = TestUtils.uid("J")
        withDisabledConfigurationTest {
            casc(
                """
                    ontrack:
                        config:
                            jira:
                                - name: $name
                                  url: https://jira.nemerosa.com
                                  user: my-user
                                  password: my-secret-token
                """.trimIndent()
            )
        }
        // Checks the JIRA configuration has been registered
        asAdmin {
            val configurations = jiraConfigurationService.configurations
            assertEquals(1, configurations.size, "Only one configuration is defined")
            val configuration = configurations.first()
            assertEquals(name, configuration.name)
            assertEquals("https://jira.nemerosa.com", configuration.url)
            assertEquals("my-user", configuration.user)
            assertEquals("my-secret-token", configuration.password)
        }
    }

}