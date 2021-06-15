package net.nemerosa.ontrack.extension.bitbucket.cloud.configuration

import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BitbucketCloudConfigurationControllerTest {

    private lateinit var bitbucketCloudConfigurationService: BitbucketCloudConfigurationService
    private lateinit var bitbucketCloudConfigurationController: BitbucketCloudConfigurationController

    @Before
    fun init() {
        bitbucketCloudConfigurationService = mockk()
        bitbucketCloudConfigurationController = BitbucketCloudConfigurationController(
            configurationService = bitbucketCloudConfigurationService,
            securityService = mockk(),
        )
    }

    @Test
    fun `Form for a creation`() {
        val form = bitbucketCloudConfigurationController.getConfigurationForm()
        assertNotNull(form.getField("name")) {
            assertNull(it.value)
        }
        assertNotNull(form.getField("workspace")) {
            assertNull(it.value)
        }
        assertNotNull(form.getField("user")) {
            assertNull(it.value)
        }
        assertNotNull(form.getField("password")) {
            assertNull(it.value)
        }
    }

    @Test
    fun `Form for an update`() {
        val config = BitbucketCloudConfiguration(
            name = "Config",
            workspace = "my-workspace",
            user = "user",
            password = "app-password",
        )
        every { bitbucketCloudConfigurationService.getConfiguration("Config") } returns config
        val form = bitbucketCloudConfigurationController.updateConfigurationForm("Config")
        assertNotNull(form.getField("name")) {
            assertEquals("Config", it.value)
        }
        assertNotNull(form.getField("workspace")) {
            assertEquals("my-workspace", it.value)
        }
        assertNotNull(form.getField("user")) {
            assertEquals("user", it.value)
        }
        assertNotNull(form.getField("password")) {
            assertNull(it.value)
        }
    }

}