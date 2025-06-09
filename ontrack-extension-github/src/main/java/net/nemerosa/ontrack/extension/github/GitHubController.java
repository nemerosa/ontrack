package net.nemerosa.ontrack.extension.github;

import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("extension/github")
public class GitHubController extends AbstractExtensionController<GitHubExtensionFeature> {

    private final GitHubConfigurationService configurationService;
    private final SecurityService securityService;

    @Autowired
    public GitHubController(GitHubExtensionFeature feature,
                            GitHubConfigurationService configurationService,
                            SecurityService securityService) {
        super(feature);
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
                .with("configurations", uri(on(getClass()).getConfigurations()), securityService.isGlobalFunctionGranted(GlobalSettings.class))
                ;
    }

    /**
     * Gets the configurations
     */
    @RequestMapping(value = "configurations", method = RequestMethod.GET)
    public Resources<GitHubEngineConfiguration> getConfigurations() {
        return Resources.of(
                configurationService.getConfigurations(),
                uri(on(getClass()).getConfigurations())
        )
                .with("_test", uri(on(getClass()).testConfiguration(null)), securityService.isGlobalFunctionGranted(GlobalSettings.class))
                ;
    }

    /**
     * Test for a configuration
     */
    @RequestMapping(value = "configurations/test", method = RequestMethod.POST)
    public ConnectionResult testConfiguration(@RequestBody GitHubEngineConfiguration configuration) {
        return configurationService.test(configuration);
    }

    /**
     * Gets the configuration descriptors
     */
    @RequestMapping(value = "configurations/descriptors", method = RequestMethod.GET)
    public Resources<ConfigurationDescriptor> getConfigurationsDescriptors() {
        return Resources.of(
                configurationService.getConfigurationDescriptors(),
                uri(on(getClass()).getConfigurationsDescriptors())
        );
    }

    /**
     * Creating a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.POST)
    public GitHubEngineConfiguration newConfiguration(@RequestBody GitHubEngineConfiguration configuration) {
        return configurationService.newConfiguration(configuration);
    }

    /**
     * Gets one configuration
     */
    @RequestMapping(value = "configurations/{name:.*}", method = RequestMethod.GET)
    public GitHubEngineConfiguration getConfiguration(@PathVariable String name) {
        return configurationService.getConfiguration(name);
    }

    /**
     * Deleting one configuration
     */
    @RequestMapping(value = "configurations/{name:.*}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Ack deleteConfiguration(@PathVariable String name) {
        configurationService.deleteConfiguration(name);
        return Ack.OK;
    }

    /**
     * Updating one configuration
     */
    @RequestMapping(value = "configurations/{name:.*}/update", method = RequestMethod.PUT)
    public GitHubEngineConfiguration updateConfiguration(@PathVariable String name, @RequestBody GitHubEngineConfiguration configuration) {
        configurationService.updateConfiguration(name, configuration);
        return getConfiguration(name);
    }
}
