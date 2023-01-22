package net.nemerosa.ontrack.extension.casc.upload

import net.nemerosa.ontrack.extension.casc.CascConfigurationProperties
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UploadCascLoaderIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var cascUploadService: CascUploadService

    @Autowired
    private lateinit var cascConfigurationProperties: CascConfigurationProperties

    @Autowired
    private lateinit var uploadCascLoader: UploadCascLoader

    @Test
    fun `Not active when not enabled`() {
        cascConfigurationProperties.upload.enabled = false
        asAdmin {
            cascUploadService.upload(sampleYaml)
            assertTrue(uploadCascLoader.loadCascFragments().isEmpty(), "No CasC being loaded")
        }
    }

    @Test
    fun `Loading from the storage`() {
        cascConfigurationProperties.upload.enabled = true
        asAdmin {
            cascUploadService.upload(sampleYaml)
            assertEquals(
                listOf(sampleYaml),
                uploadCascLoader.loadCascFragments()
            )
        }
    }

    companion object {
        private val sampleYaml = """
            ontrack:
              config:
                settings:
                  security:
                    grantProjectViewToAll: true
                    grantProjectParticipationToAll: true
        """.trimIndent()
    }

}