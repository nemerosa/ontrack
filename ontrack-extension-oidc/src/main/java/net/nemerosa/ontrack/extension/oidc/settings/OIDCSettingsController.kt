package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@RestController
@RequestMapping("/extension/oidc/providers")
class OIDCSettingsController(
        private val oidcSettingsService: OIDCSettingsService
) : AbstractResourceController() {

    /**
     * List of providers
     */
    @GetMapping("")
    fun getProviders(): Resources<OntrackOIDCProvider> =
            Resources.of(
                    oidcSettingsService.providers,
                    uri(on(OIDCSettingsController::class.java).getProviders())
            )

}