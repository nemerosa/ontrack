package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecoratorTestSupport
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class OntrackOIDCProviderResourceDecoratorIT : AbstractResourceDecoratorTestSupport() {

    @Autowired
    private lateinit var oidcSettingsService: OIDCSettingsService

    @Autowired
    private lateinit var decorator: OntrackOIDCProviderResourceDecorator

    @Test
    fun `Rights to update and delete providers`() {
        asUserWith<GlobalSettings> {
            val provider = OntrackOIDCProviderFixtures.testProvider(uid("P"))
            oidcSettingsService.createProvider(provider)
            provider.decorate(decorator) {
                assertLinkPresent("_update")
                assertLinkPresent("_delete")
            }
        }
    }

    @Test
    fun `Image link not present when no image`() {
        asUserWith<GlobalSettings> {
            val provider = OntrackOIDCProviderFixtures.testProvider(uid("P"))
            oidcSettingsService.createProvider(provider)
            provider.decorate(decorator) {
                assertLinkNotPresent("_image")
                assertLinkPresent("_imageUpdate")
            }
        }
    }

    @Test
    fun `Image link present when image`() {
        val bytes = OntrackOIDCProviderResourceDecoratorIT::class.java
                .getResourceAsStream("/image.png")
                .readAllBytes()
        asUserWith<GlobalSettings> {
            val provider = OntrackOIDCProviderFixtures.testProvider(uid("P"))
            oidcSettingsService.createProvider(provider)
            oidcSettingsService.setProviderImage(provider.id, Document(
                    "image/png",
                    bytes
            ))
            provider.decorate(decorator) {
                assertLinkPresent("_image")
                assertLinkPresent("_imageUpdate")
            }
        }
    }

}