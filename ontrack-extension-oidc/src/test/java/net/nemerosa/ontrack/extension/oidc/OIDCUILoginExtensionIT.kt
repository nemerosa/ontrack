package net.nemerosa.ontrack.extension.oidc

import net.nemerosa.ontrack.extension.api.UILogin
import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.extension.oidc.settings.OntrackOIDCProviderFixtures
import net.nemerosa.ontrack.it.AbstractDSLTestJUnit4Support
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

class OIDCUILoginExtensionIT : AbstractDSLTestJUnit4Support() {

    @Autowired
    private lateinit var service: OIDCSettingsService

    @Autowired
    protected lateinit var uiLoginExtension: OIDCUILoginExtension

    @Before
    fun cleanup() {
        asAdmin {
            service.providers.forEach {
                service.deleteProvider(it.id)
            }
        }
    }

    @Test
    fun `UI login extension without an image`() {
        val id = TestUtils.uid("I")
        asAdmin {
            service.createProvider(OntrackOIDCProviderFixtures.testProvider(id = id))
        }
        asAnonymous {
            val login = uiLoginExtension.contributions.first { it.id == id }
            assertFalse(login.image, "No image associated")
            assertNull(login.imageLoader(), "No image being loaded")
        }
    }

    @Test
    fun `UI login extension with an image`() {
        val id = TestUtils.uid("I")
        val image = OntrackOIDCProviderFixtures.image()
        asAdmin {
            service.createProvider(OntrackOIDCProviderFixtures.testProvider(id = id))
            service.setProviderImage(id, image)
        }
        asAnonymous {
            val login = uiLoginExtension.contributions.first { it.id == id }
            assertTrue(login.image, "Image associated")
            assertNotNull(login.imageLoader(), "Image being loaded") {
                assertEquals(image, it)
            }
        }
    }

    @Test
    fun `UI login extension after creation and after deletion of a provider`() {
        val id = TestUtils.uid("I")
        asAdmin {
            service.createProvider(OntrackOIDCProviderFixtures.testProvider(id = id, description = "Some link"))
        }
        asAnonymous {
            val logins = uiLoginExtension.contributions
            assertEquals(
                    listOf(
                            UILogin(id, "/oauth2/authorization/$id", "Test", "Some link")
                    ),
                    logins
            )
        }
        asAdmin {
            service.deleteProvider(id)
        }
        asAnonymous {
            val logins = uiLoginExtension.contributions
            assertTrue(logins.isEmpty())
        }
    }

}