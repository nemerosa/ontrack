package net.nemerosa.ontrack.extension.casc

import net.nemerosa.ontrack.extension.casc.context.core.admin.MockAdminContext
import net.nemerosa.ontrack.model.settings.HomePageSettings
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
        cascConfigurationProperties.secrets.directory = secretsRootDir.absolutePathString()
    }

    @Test
    fun `Rendering as YAML`() {
        asAdmin {
            withSettings<HomePageSettings> {
                settingsManagerService.saveSettings(
                    HomePageSettings(
                        maxBranches = 2,
                        maxProjects = 200,
                    )
                )
                val yaml = cascService.renderAsYaml()
                assertTrue("maxBranches: 2" in yaml)
                assertTrue("maxProjects: 200" in yaml)
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
                val json = cascService.renderAsJson()
                assertEquals(2,
                    json.path("ontrack").path("config").path("settings").path("home-page").path("maxBranches").asInt())
                assertEquals(200,
                    json.path("ontrack").path("config").path("settings").path("home-page").path("maxProjects").asInt())
            }
        }
    }

    @Test
    fun `Rendering with secrets`() {
        secretsRootDir
            .resolve("my-mock")
            .createDirectories()
            .resolve("password")
            .writeText("my-password")
        asAdmin {
            mockAdminContext.data = null
            casc("""
                ontrack:
                    admin:
                        mock:
                            username: my-user
                            password: {{ secret.my-mock.password }}
            """.trimIndent())
            assertEquals("my-user", mockAdminContext.data?.username)
            assertEquals("my-password", mockAdminContext.data?.password)
        }
    }

}