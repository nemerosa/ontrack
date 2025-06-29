package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.extension.casc.context.core.admin.MockAdminContext
import net.nemerosa.ontrack.extension.casc.support.SampleSettings
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.Roles
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestPropertySource(
    properties = [
        "ontrack.config.casc.secrets.type=file",
    ]
)
class CascServiceIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var cascService: CascService

    @Autowired
    private lateinit var mockAdminContext: MockAdminContext

    @Autowired
    private lateinit var cascConfigurationProperties: CascConfigurationProperties

    private lateinit var secretsRootDir: Path

    @BeforeEach
    fun before() {
        secretsRootDir = createTempDirectory()
        secretsRootDir
            .resolve("my-mock")
            .createDirectories()
            .resolve("password")
            .writeText("my-password")
        secretsRootDir
            .resolve("my-mock")
            .createDirectories()
            .resolve("key")
            .writeText(
                """
                Secret
                on multiple
                lines
            """.trimIndent()
            )
        cascConfigurationProperties.secrets.directory = secretsRootDir.absolutePathString()
    }

    @Test
    fun `Rendering as YAML`() {
        asAdmin {
            renderAsYaml()
        }
    }

    @Test
    fun `Rendering as YAML allowed for users with global settings`() {
        asUser().with(GlobalSettings::class.java).call {
            renderAsYaml()
        }
    }

    @Test
    fun `Rendering as YAML allowed for admin users`() {
        asAccountWithGlobalRole(Roles.GLOBAL_ADMINISTRATOR) {
            renderAsYaml()
        }
    }

    private fun renderAsYaml() {
        withSettings<SampleSettings> {
            settingsManagerService.saveSettings(
                SampleSettings(
                    maxProjects = 200,
                    enabled = true,
                )
            )
            val yaml = cascService.renderAsYaml()
            assertTrue("maxProjects: 200" in yaml)
            assertTrue("enabled: true" in yaml)
        }
    }

    @Test
    fun `Rendering as JSON`() {
        asAdmin {
            withSettings<SampleSettings> {
                settingsManagerService.saveSettings(
                    SampleSettings(
                        maxProjects = 200,
                        enabled = true,
                    )
                )
                val json = cascService.renderAsJson()
                assertEquals(
                    200,
                    json.path("ontrack").path("config").path("settings").path("sample").path("maxProjects").asInt()
                )
                assertEquals(
                    true,
                    json.path("ontrack").path("config").path("settings").path("sample").path("enabled").asBoolean()
                )
            }
        }
    }

    @Test
    fun `Rendering with secrets`() {
        asAdmin {
            mockAdminContext.data = null
            casc(
                """
                ontrack:
                    admin:
                        mock:
                            username: my-user
                            password: {{ secret.my-mock.password }}
            """.trimIndent()
            )
            assertEquals("my-user", mockAdminContext.data?.username)
            assertEquals("my-password", mockAdminContext.data?.password)
        }
    }

    @Test
    fun `Rendering with multiline secrets`() {
        asAdmin {
            mockAdminContext.data = null
            casc(
                """
                ontrack:
                    admin:
                        mock:
                            username: my-user
                            password: |-
                                {{ secret.my-mock.key }}
            """.trimIndent()
            )
            assertEquals("my-user", mockAdminContext.data?.username)
            assertEquals(
                """
                Secret
                on multiple
                lines
            """.trimIndent(), mockAdminContext.data?.password
            )
        }
    }

}