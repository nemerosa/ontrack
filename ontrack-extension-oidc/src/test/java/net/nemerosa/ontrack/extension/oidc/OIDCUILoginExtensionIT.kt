package net.nemerosa.ontrack.extension.oidc

import net.nemerosa.ontrack.extension.api.UILogin
import net.nemerosa.ontrack.extension.oidc.settings.OIDCSettingsService
import net.nemerosa.ontrack.extension.oidc.settings.OntrackOIDCProvider
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OIDCUILoginExtensionIT : AbstractDSLTestSupport() {

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
    fun `UI login extension after creation and after deletion of a provider`() {
        val id = TestUtils.uid("I")
        asAdmin {
            service.createProvider(OntrackOIDCProvider(id, "Test", "Some link", "", "", "", null))
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