package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * API for the LDAP mappings.
 */
@RestController
@RequestMapping("/extension/ldap")
public class LDAPController extends AbstractExtensionController<LDAPExtensionFeature> {

    private final SecurityService securityService;
    private final AccountService accountService;
    private final AccountGroupMappingService accountGroupMappingService;

    @Autowired
    public LDAPController(
            LDAPExtensionFeature feature,
            SecurityService securityService,
            AccountService accountService,
            AccountGroupMappingService accountGroupMappingService
    ) {
        super(feature);
        this.securityService = securityService;
        this.accountService = accountService;
        this.accountGroupMappingService = accountGroupMappingService;
    }

    @Override
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resource<ExtensionFeatureDescription> getDescription() {
        return Resource.of(
                feature.getFeatureDescription(),
                uri(on(getClass()).getDescription())
        )
                // List of mappings
                .with(
                        "_mappings",
                        uri(on(getClass()).getMappings()),
                        securityService.isGlobalFunctionGranted(AccountGroupManagement.class)
                )
                ;
    }

    /**
     * Gets the list of mappings
     */
    @RequestMapping(value = "ldap-mapping", method = RequestMethod.GET)
    public Resources<LDAPMapping> getMappings() {
        securityService.checkGlobalFunction(AccountGroupManagement.class);
        return Resources.of(
                accountGroupMappingService.getMappings(LDAPExtensionFeature.LDAP_GROUP_MAPPING)
                        .stream().map(LDAPMapping::of),
                uri(on(getClass()).getMappings())
        )
                .with(
                        Link.CREATE,
                        uri(on(getClass()).getMappingCreationForm()),
                        securityService.isGlobalFunctionGranted(AccountManagement.class)
                )
                ;
    }

    /**
     * Gets the form for the creation of a mapping
     */
    @RequestMapping(value = "ldap-mapping/create", method = RequestMethod.GET)
    public Form getMappingCreationForm() {
        securityService.checkGlobalFunction(AccountGroupManagement.class);
        return AccountGroupMapping.form(
                accountService.getAccountGroups()
        );
    }

    /**
     * Creates a mapping
     */
    @RequestMapping(value = "ldap-mapping/create", method = RequestMethod.POST)
    public LDAPMapping newMapping(@RequestBody AccountGroupMappingInput input) {
        securityService.checkGlobalFunction(AccountGroupManagement.class);
        return LDAPMapping.of(accountGroupMappingService.newMapping(LDAPExtensionFeature.LDAP_GROUP_MAPPING, input));
    }

    /**
     * Gets a mapping
     */
    @RequestMapping(value = "ldap-mapping/{id}", method = RequestMethod.GET)
    public LDAPMapping getMapping(@PathVariable ID id) {
        securityService.checkGlobalFunction(AccountGroupManagement.class);
        return LDAPMapping.of(accountGroupMappingService.getMapping(LDAPExtensionFeature.LDAP_GROUP_MAPPING, id));
    }

    /**
     * Gets the form to update a mapping
     */
    @RequestMapping(value = "ldap-mapping/{id}/update", method = RequestMethod.GET)
    public Form getMappingUpdateForm(@PathVariable ID id) {
        securityService.checkGlobalFunction(AccountGroupManagement.class);
        return accountGroupMappingService.getMapping(LDAPExtensionFeature.LDAP_GROUP_MAPPING, id).asForm(
                accountService.getAccountGroups()
        );
    }

    /**
     * Updating a mapping
     */
    @RequestMapping(value = "ldap-mapping/{id}/update", method = RequestMethod.PUT)
    public LDAPMapping updateMapping(@PathVariable ID id, @RequestBody AccountGroupMappingInput input) {
        securityService.checkGlobalFunction(AccountGroupManagement.class);
        return LDAPMapping.of(accountGroupMappingService.updateMapping(
                LDAPExtensionFeature.LDAP_GROUP_MAPPING,
                id,
                input
        ));
    }

    /**
     * Deleting a mapping
     */
    @RequestMapping(value = "ldap-mapping/{id}/delete", method = RequestMethod.DELETE)
    public Ack deleteMapping(@PathVariable ID id) {
        securityService.checkGlobalFunction(AccountGroupManagement.class);
        return accountGroupMappingService.deleteMapping(
                LDAPExtensionFeature.LDAP_GROUP_MAPPING,
                id
        );
    }
}
