package net.nemerosa.ontrack.extension.stash.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BitbucketServerConfigurationContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var stashConfigurationService: StashConfigurationService

    @Test
    fun `Defining a Bitbucket Server configuration`() {
        val name = TestUtils.uid("BS")
        withDisabledConfigurationTest {
            casc(
                """
                    ontrack:
                        config:
                            bitbucketServer:
                                - name: $name
                                  url: https://bitbucket.nemerosa.com
                                  user: my-user
                                  password: my-secret-password
                """.trimIndent()
            )
        }
        // Checks the Bitbucket Server configuration has been registered
        asAdmin {
            val configurations = stashConfigurationService.configurations
            assertEquals(1, configurations.size, "Only one configuration is defined")
            val configuration = configurations.first()
            assertEquals(name, configuration.name)
            assertEquals("https://bitbucket.nemerosa.com", configuration.url)
            assertEquals("my-user", configuration.user)
            assertEquals("my-secret-password", configuration.password)
        }
    }

}