package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.form.Url
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
                    oidcSettingsService.providers.map {
                        it.obfuscate()
                    },
                    uri(on(OIDCSettingsController::class.java).getProviders())
            )
                    .with(Link.CREATE, uri(on(OIDCSettingsController::class.java).getCreationForm()))

    /**
     * Get the form to add a provider
     */
    @GetMapping("create")
    fun getCreationForm(): Form = getForm(null)

    /**
     * Creation of a provider
     */
    @PostMapping("create")
    fun createProvider(@RequestBody input: OntrackOIDCProvider) =
            ResponseEntity.ok(
                    oidcSettingsService.createProvider(input)
            )

    /**
     * Deletion of a provider
     */
    @DeleteMapping("{id}")
    fun deleteProvider(@PathVariable id: String): Ack =
            oidcSettingsService.deleteProvider(id)

    private fun getForm(provider: OntrackOIDCProvider?): Form = Form.create()
            .with(
                    Text.of("id").label("ID")
                            .help("Unique ID for this provider")
                            .value(provider?.id)
            )
            .with(
                    Text.of("name").label("Name")
                            .help("Display name for this provider")
                            .value(provider?.name)
            )
            .with(
                    Text.of("description").label("Description")
                            .help("Tooltip for this provider")
                            .value(provider?.description)
            )
            .with(
                    Url.of("issuerId").label("Issuer ID")
                            .help("OIDC issueId URL")
                            .value(provider?.issuerId)
            )
            .with(
                    Text.of("clientId").label("Client ID")
                            .help("OIDC client ID")
                            .value(provider?.clientId)
            )
            .with(
                    Password.of("clientSecret").label("Client secret")
                            .help("OIDC client secret")
            )

}