package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.form.Url
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.Resource
import net.nemerosa.ontrack.ui.resource.Resources
import net.nemerosa.ontrack.ui.support.UIUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

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
    fun createProvider(@RequestBody @Valid input: OntrackOIDCProvider) =
            ResponseEntity.ok(
                    oidcSettingsService.createProvider(input)
            )

    /**
     * Deletion of a provider
     */
    @DeleteMapping("{id}")
    fun deleteProvider(@PathVariable id: String): Ack =
            oidcSettingsService.deleteProvider(id)

    /**
     * Get the form to update a provider
     */
    @GetMapping("update/{id}")
    fun getUpdateForm(@PathVariable id: String): Form {
        val provider = oidcSettingsService.getProviderById(id)
                ?.obfuscate()
                ?: throw OntrackOIDCProviderIDNotFoundException(id)
        return getForm(provider)
    }

    /**
     * Updates a provider
     */
    @PutMapping("update/{id}")
    fun updateProvider(@PathVariable id: String, @RequestBody @Valid input: OntrackOIDCProvider): OntrackOIDCProvider =
            if (input.id != id) {
                throw OntrackOIDCProviderIDMustMatchException(id, input.id)
            } else {
                oidcSettingsService.updateProvider(input).obfuscate()
            }

    /**
     * Gets the image, if any, associated to a provider.
     */
    @GetMapping("{id}/image")
    fun getProviderImage(response: HttpServletResponse?, @PathVariable id: String): Resource<Document> {
        val document = oidcSettingsService.getProviderImage(id) ?: Document.EMPTY
        response?.also { UIUtils.setupDefaultImageCache(response, document) }
        return Resource.of(document, uri(on(this::class.java).getProviderImage(response, id)))
    }

    @PostMapping("{id}/image")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun setProviderImage(@PathVariable id: String, @RequestParam file: MultipartFile) {
        val contentType = file.contentType
        if (contentType != null) {
            oidcSettingsService.setProviderImage(id, Document(
                    contentType,
                    file.bytes
            ))
        }
    }

    private fun getForm(provider: OntrackOIDCProvider?): Form = Form.create()
            .with(
                    Text.of("id").label("ID")
                            .help("Unique ID for this provider")
                            .value(provider?.id)
                            .readOnly(provider != null)
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
                            .value("")
                            .optional()
            )
            .with(
                    Text.of("groupFilter").label("Group filter")
                            .help("Regular expression used to filter groups associated with the OIDC user.")
                            .value(provider?.groupFilter)
            )

}