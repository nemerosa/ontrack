package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.extension.api.ExtensionFeatureDescription;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.security.AccountGroupMapping;
import net.nemerosa.ontrack.model.security.AccountGroupMappingService;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * API for the LDAP mappings.
 */
@RestController
@RequestMapping("/extension/ldap")
public class LDAPController extends AbstractExtensionController<LDAPExtensionFeature> {

    private final SecurityService securityService;
    private final AccountGroupMappingService accountGroupMappingService;

    @Autowired
    public LDAPController(
            LDAPExtensionFeature feature,
            SecurityService securityService,
            AccountGroupMappingService accountGroupMappingService
    ) {
        super(feature);
        this.securityService = securityService;
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
                        securityService.isGlobalFunctionGranted(GlobalSettings.class)
                )
                ;
    }

    /**
     * Gets the list of mappings
     */
    @RequestMapping(value = "ldap-mapping", method = RequestMethod.GET)
    public Resources<AccountGroupMapping> getMappings() {
        securityService.checkGlobalFunction(GlobalSettings.class);
        return Resources.of(
                accountGroupMappingService.getMappings(LDAPExtensionFeature.LDAP_GROUP_MAPPING),
                uri(on(getClass()).getMappings())
        );
    }
}
