package net.nemerosa.ontrack.extension.issues.combined;

import net.nemerosa.ontrack.extension.api.ExtensionFeatureDescription;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RequestMapping("extension/issue/combined")
@RestController
public class CombinedIssueServiceController extends AbstractExtensionController<CombinedIssueServiceExtensionFeature> {

    private final CombinedIssueServiceConfigurationService configurationService;
    private final SecurityService securityService;

    @Autowired
    public CombinedIssueServiceController(CombinedIssueServiceExtensionFeature extensionFeature, CombinedIssueServiceConfigurationService configurationService, SecurityService securityService) {
        super(extensionFeature);
        this.configurationService = configurationService;
        this.securityService = securityService;
    }

    @Override
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Resource<ExtensionFeatureDescription> getDescription() {
        return Resource.of(
                feature.getFeatureDescription(),
                uri(MvcUriComponentsBuilder.on(getClass()).getDescription())
        )
                .with("configurations", uri(on(getClass()).getConfigurationList()), securityService.isGlobalFunctionGranted(GlobalSettings.class))
                ;
    }

    /**
     * Gets the list of configurations
     */
    @RequestMapping(value = "configurations", method = RequestMethod.GET)
    public Resources<CombinedIssueServiceConfiguration> getConfigurationList() {
        return Resources.of(
                configurationService.getConfigurationList(),
                uri(on(getClass()).getConfigurationList())
        )
                .with(Link.CREATE, uri(on(getClass()).getConfigurationForm()))
                ;
    }

    /**
     * Form for a new configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.GET)
    public Form getConfigurationForm() {
        return CombinedIssueServiceConfiguration.form(
                configurationService.getAvailableIssueServiceConfigurations()
        );
    }
}
