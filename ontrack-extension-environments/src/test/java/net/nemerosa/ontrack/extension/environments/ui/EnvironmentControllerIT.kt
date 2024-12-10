package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.environments.EnvironmentTestSupport
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.resourceBase64
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletResponse
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EnvironmentControllerIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var environmentController: EnvironmentController

    @Autowired
    private lateinit var environmentTestSupport: EnvironmentTestSupport

    @Autowired
    private lateinit var environmentService: EnvironmentService

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun `Changing the image for an environment`() {
        val image = resourceBase64("/images/environment.png")
        asAdmin {
            environmentTestSupport.withEnvironment { environment ->
                environmentController.putEnvironmentImage(
                    environmentId = environment.id,
                    image = image,
                )
                // Checks that the environment is flagged as having an image
                assertTrue(
                    environmentService.getById(environment.id).image,
                    "Environment has an image"
                )
                // Downloading the image
                val response = MockHttpServletResponse()
                val document = environmentController.getEnvironmentImage(response, environment.id)
                assertEquals("image/png", document.type)
                assertEquals(image, Base64.encode(document.content))
            }
        }
    }

}