package net.nemerosa.ontrack.extension.git.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.git.support.GitConnectionConfig
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.yaml.Yaml
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GitConfigCascContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var gitConfigService: GitConfigService

    @Autowired
    private lateinit var gitConfigCascContext: GitConfigCascContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Autowired
    private lateinit var extensionsContext: net.nemerosa.ontrack.extension.casc.context.extensions.ExtensionsContext

    @BeforeEach
    fun setup() {
        asAdmin {
            gitConfigService.saveGitConnectionConfig(GitConnectionConfig.default)
        }
    }

    @Test
    fun `CasC schema`() {
        assertEquals("git", gitConfigCascContext.field)
        val render = extensionsContext.render()
        assertTrue(render.has("git"), "git field should be present in extensions context")
    }

    @Test
    fun `Running directly`() {
        val node = Yaml().read("""
            retryConfiguration:
              retries:
                - connectionError: true
                  retryLimit: 120
        """.trimIndent()).single()

        gitConfigCascContext.run(node, emptyList())

        val config = gitConfigService.gitConnectionConfig
        assertEquals(1, config.retries.size)
        assertTrue(config.retries[0].connectionError)
        assertEquals(120, config.retries[0].retryLimit)
    }

    @Test
    fun `Typical configuration`() {
        casc(
            """
            ontrack:
              extensions:
                git:
                  retryConfiguration:
                    retries:
                      - connectionError: true
                        retryLimit: 120
                      - httpCode: "5[0-9]{2}"
                      - httpCode: "409"
                        errorMessage: ".*Rule was unable to be completed.*"
                        retryLimit: 2
                        retryInterval: 120
            """.trimIndent()
        )

        val config = gitConfigService.gitConnectionConfig
        val retries = config.retries
        assertEquals(3, retries.size)

        retries[0].apply {
            assertTrue(connectionError)
            assertEquals(120, retryLimit)
            assertEquals(null, retryInterval)
        }

        retries[1].apply {
            assertEquals("5[0-9]{2}", httpCode)
            assertEquals(null, retryLimit)
            assertEquals(null, retryInterval)
        }

        retries[2].apply {
            assertEquals("409", httpCode)
            assertEquals(".*Rule was unable to be completed.*", errorMessage)
            assertEquals(2, retryLimit)
            assertEquals(Duration.ofSeconds(120), retryInterval)
        }
    }

    @Test
    fun `Multiple configurations`() {
        casc(
            """
            ontrack:
              extensions:
                git:
                  retryConfiguration:
                    retries:
                      - httpCode: "503"
                        retryLimit: 5
                      - connectionError: true
                        retryLimit: 10
            """.trimIndent()
        )

        val config = gitConfigService.gitConnectionConfig
        assertEquals(2, config.retries.size)

        config.retries[0].apply {
            assertEquals("503", httpCode)
            assertEquals(5, retryLimit)
        }

        config.retries[1].apply {
            assertTrue(connectionError)
            assertEquals(10, retryLimit)
        }
    }

    @Test
    fun `Default values`() {
        casc(
            """
            ontrack:
              extensions:
                git:
                  retryConfiguration:
                    retries:
                      - {}
            """.trimIndent()
        )

        val config = gitConfigService.gitConnectionConfig
        assertEquals(1, config.retries.size)

        config.retries[0].apply {
            assertEquals("", httpCode)
            assertEquals(".*", errorMessage)
            assertEquals(null, retryLimit)
            assertEquals(null, retryInterval)
            assertEquals(false, connectionError)
        }

    }

}
