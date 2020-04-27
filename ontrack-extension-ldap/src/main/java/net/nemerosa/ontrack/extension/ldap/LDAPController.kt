package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.extension.support.AbstractExtensionController
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.Resource
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder

/**
 * API for the LDAP mappings.
 */
@RestController
@RequestMapping("/extension/ldap")
class LDAPController(
        feature: LDAPExtensionFeature,
        private val securityService: SecurityService,
        private val accountService: AccountService,
        private val accountGroupMappingService: AccountGroupMappingService
) : AbstractExtensionController<LDAPExtensionFeature>(feature) {

    @GetMapping("")
    override fun getDescription(): Resource<ExtensionFeatureDescription> {
        @Suppress("RecursivePropertyAccessor")
        return Resource.of(
                feature.featureDescription,
                uri(MvcUriComponentsBuilder.on(javaClass).description)
        ) // List of mappings
                .with(
                        "_mappings",
                        uri(MvcUriComponentsBuilder.on(javaClass).getMappings()),
                        securityService.isGlobalFunctionGranted(AccountGroupManagement::class.java)
                )
    }

    /**
     * Gets the list of mappings
     */
    @GetMapping("ldap-mapping")
    fun getMappings(): Resources<LDAPMapping> {
        securityService.checkGlobalFunction(AccountGroupManagement::class.java)
        return Resources.of(
                accountGroupMappingService.getMappings(LDAPAuthenticationSourceProvider.SOURCE.id).map {
                    LDAPMapping.of(it)
                },
                uri(MvcUriComponentsBuilder.on(javaClass).getMappings())
        )
                .with(
                        Link.CREATE,
                        uri(MvcUriComponentsBuilder.on(javaClass).getMappingCreationForm()),
                        securityService.isGlobalFunctionGranted(AccountGroupManagement::class.java)
                )
    }

    /**
     * Gets the form for the creation of a mapping
     */
    @GetMapping("ldap-mapping/create")
    fun getMappingCreationForm(): Form {
        securityService.checkGlobalFunction(AccountGroupManagement::class.java)
        return AccountGroupMapping.form(
                accountService.accountGroups
        )
    }

    /**
     * Creates a mapping
     */
    @PostMapping("ldap-mapping/create")
    fun newMapping(@RequestBody input: AccountGroupMappingInput): LDAPMapping {
        securityService.checkGlobalFunction(AccountGroupManagement::class.java)
        return LDAPMapping.of(accountGroupMappingService.newMapping(LDAPAuthenticationSourceProvider.SOURCE.id, input))
    }

    /**
     * Gets a mapping
     */
    @GetMapping("ldap-mapping/{id}")
    fun getMapping(@PathVariable id: ID): LDAPMapping {
        securityService.checkGlobalFunction(AccountGroupManagement::class.java)
        return LDAPMapping.of(accountGroupMappingService.getMapping(LDAPAuthenticationSourceProvider.SOURCE.id, id))
    }

    /**
     * Gets the form to update a mapping
     */
    @GetMapping("ldap-mapping/{id}/update")
    fun getMappingUpdateForm(@PathVariable id: ID): Form {
        securityService.checkGlobalFunction(AccountGroupManagement::class.java)
        return accountGroupMappingService.getMapping(LDAPAuthenticationSourceProvider.SOURCE.id, id).asForm(
                accountService.accountGroups
        )
    }

    /**
     * Updating a mapping
     */
    @PutMapping("ldap-mapping/{id}/update")
    fun updateMapping(@PathVariable id: ID, @RequestBody input: AccountGroupMappingInput): LDAPMapping {
        securityService.checkGlobalFunction(AccountGroupManagement::class.java)
        return LDAPMapping.of(accountGroupMappingService.updateMapping(
                LDAPAuthenticationSourceProvider.SOURCE.id,
                id,
                input
        ))
    }

    /**
     * Deleting a mapping
     */
    @DeleteMapping("ldap-mapping/{id}/delete")
    fun deleteMapping(@PathVariable id: ID): Ack {
        securityService.checkGlobalFunction(AccountGroupManagement::class.java)
        return accountGroupMappingService.deleteMapping(
                LDAPAuthenticationSourceProvider.SOURCE.id,
                id
        )
    }

}