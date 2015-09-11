package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.extension.api.ExtensionFeatureDescription;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
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
                        securityService.isGlobalFunctionGranted(AccountManagement.class)
                )
                ;
    }

    /**
     * Gets the list of mappings
     */
    @RequestMapping(value = "ldap-mapping", method = RequestMethod.GET)
    public Resources<AccountGroupMapping> getMappings() {
        securityService.checkGlobalFunction(AccountManagement.class);
        return Resources.of(
                accountGroupMappingService.getMappings(LDAPExtensionFeature.LDAP_GROUP_MAPPING),
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
        securityService.checkGlobalFunction(AccountManagement.class);
        return AccountGroupMapping.form(
                accountService.getAccountGroups()
        );
    }

    /**
     * Creates a mapping
     */
    @RequestMapping(value = "ldap-mapping/create", method = RequestMethod.POST)
    public AccountGroupMapping newMapping(@RequestBody AccountGroupMappingInput input) {
        securityService.checkGlobalFunction(AccountManagement.class);
        return accountGroupMappingService.newMapping(LDAPExtensionFeature.LDAP_GROUP_MAPPING, input);
    }

    /**
     * Gets a mapping
     */
    @RequestMapping(value = "ldap-mapping/{id}", method = RequestMethod.GET)
    public AccountGroupMapping getMapping(@PathVariable ID id) {
        securityService.checkGlobalFunction(AccountManagement.class);
        return accountGroupMappingService.getMapping(LDAPExtensionFeature.LDAP_GROUP_MAPPING, id);
    }
}
