package net.nemerosa.ontrack.extension.casc.ui

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.casc.CascConfigurationProperties
import net.nemerosa.ontrack.extension.casc.CascLoadingService
import net.nemerosa.ontrack.extension.casc.schema.json.CascJsonSchemaService
import net.nemerosa.ontrack.extension.casc.upload.CascUploadService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.multipart.MultipartFile
import kotlin.test.assertFailsWith

class CascControllerTest {

    private lateinit var loadingService: CascLoadingService
    private lateinit var uploadService: CascUploadService
    private lateinit var configurationProperties: CascConfigurationProperties

    private lateinit var controller: CascController

    @BeforeEach
    fun init() {
        loadingService = mockk(relaxed = true)
        uploadService = mockk(relaxed = true)
        configurationProperties = CascConfigurationProperties()
        val cascJsonSchemaService = mockk<CascJsonSchemaService>()
        controller = CascController(
            cascLoadingService = loadingService,
            cascConfigurationProperties = configurationProperties,
            cascUploadService = uploadService,
            cascJsonSchemaService = cascJsonSchemaService,
        )
    }

    @Test
    fun reload() {
        controller.reload()
        verify {
            loadingService.load()
        }
    }

    @Test
    fun `Upload endpoint not enabled by default`() {
        assertFailsWith<CascUploadNotEnabledException> {
            controller.upload(mockk())
        }
    }

    @Test
    fun `Upload with wrong type`() {
        configurationProperties.upload.enabled = true
        assertFailsWith<CascUploadWrongTypeException> {
            val file = mockk<MultipartFile>()
            every { file.contentType } returns "application/json"
            controller.upload(file)
        }
    }

    @Test
    fun `Uploading Casc`() {
        configurationProperties.upload.enabled = true
        val file = mockk<MultipartFile>()
        every { file.contentType } returns "application/yaml"
        val yaml = """
            ontrack:
              config:
                settings:
                  security:
                    grantProjectViewToAll: true
                    grantProjectParticipationToAll: true
        """.trimIndent()
        every { file.bytes } returns yaml.toByteArray(Charsets.UTF_8)
        controller.upload(file)
        verify {
            uploadService.upload(yaml)
        }
    }

}