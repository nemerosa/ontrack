package net.nemerosa.ontrack.extension.jenkins.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class JenkinsConfigurationCascContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var jenkinsConfigurationService: JenkinsConfigurationService

    @Test
    fun `Creating an anonymous Jenkins configuration`() {
        withDisabledConfigurationTest {
            val name = uid("j")
            casc("""
                ontrack:
                    config:
                        jenkins:
                            - name: $name
                              url: https://jenkins.example.com
            """.trimIndent())
            asAdmin {
                assertNotNull(jenkinsConfigurationService.findConfiguration(name)) {
                    assertEquals(name, it.name)
                    assertEquals("https://jenkins.example.com", it.url)
                    assertNull(it.user, "User not filled in")
                    assertNull(it.password, "Password not filled in")
                }
            }
        }
    }

    @Test
    fun `Creating a Jenkins configuration`() {
        withDisabledConfigurationTest {
            val name = uid("j")
            casc("""
                ontrack:
                    config:
                        jenkins:
                            - name: $name
                              url: https://jenkins.example.com
                              user: my-user
                              password: my-password
            """.trimIndent())
            asAdmin {
                assertNotNull(jenkinsConfigurationService.findConfiguration(name)) {
                    assertEquals(name, it.name)
                    assertEquals("https://jenkins.example.com", it.url)
                    assertEquals("my-user", it.user)
                    assertEquals("my-password", it.password)
                }
            }
        }
    }

    @Test
    fun `Deleting a Jenkins configuration`() {
        withDisabledConfigurationTest {
            val obsoleteName = uid("j")
            val name = uid("j")
            asAdmin {
                jenkinsConfigurationService.newConfiguration(
                    JenkinsConfiguration(
                        obsoleteName,
                        "https://my-old-jenkins.example.com",
                        null,
                        null
                    )
                )
            }
            casc("""
                ontrack:
                    config:
                        jenkins:
                            - name: $name
                              url: https://jenkins.example.com
                              user: my-user
                              password: my-password
            """.trimIndent())
            asAdmin {
                assertNotNull(jenkinsConfigurationService.findConfiguration(name), "New configuration has been created")
                assertNull(jenkinsConfigurationService.findConfiguration(obsoleteName),
                    "Old configuration has been deleted")
            }
        }
    }

}