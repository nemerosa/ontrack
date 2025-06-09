package net.nemerosa.ontrack.extension.artifactory;

import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfigurationService;
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

@RequestMapping("extension/artifactory")
@RestController
public class ArtifactoryController extends AbstractExtensionController<ArtifactoryExtensionFeature> {

    private final ArtifactoryConfigurationService configurationService;
    private final SecurityService securityService;

    @Autowired
    public ArtifactoryController(ArtifactoryExtensionFeature feature, ArtifactoryConfigurationService configurationService, SecurityService securityService) {
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
     * Gets the Jenkins settings
     */
    @RequestMapping(value = "configurations", method = RequestMethod.GET)
    public Resources<ArtifactoryConfiguration> getConfigurations() {
        return Resources.of(
                configurationService.getConfigurations(),
                uri(on(getClass()).getConfigurations())
        )
                .with("_test", uri(on(getClass()).testConfiguration(null)), securityService.isGlobalFunctionGranted(GlobalSettings.class))
                ;
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
     * Test for a configuration
     */
    @RequestMapping(value = "configurations/test", method = RequestMethod.POST)
    public ConnectionResult testConfiguration(@RequestBody ArtifactoryConfiguration configuration) {
        return configurationService.test(configuration);
    }

    /**
     * Creating a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.POST)
    public ArtifactoryConfiguration newConfiguration(@RequestBody ArtifactoryConfiguration configuration) {
        return configurationService.newConfiguration(configuration);
    }

    /**
     * Gets one configuration
     */
    @RequestMapping(value = "configurations/{name}", method = RequestMethod.GET)
    public ArtifactoryConfiguration getConfiguration(@PathVariable String name) {
        return configurationService.getConfiguration(name);
    }

    /**
     * Deleting one configuration
     */
    @RequestMapping(value = "configurations/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Ack deleteConfiguration(@PathVariable String name) {
        configurationService.deleteConfiguration(name);
        return Ack.OK;
    }

    /**
     * Updating one configuration
     */
    @RequestMapping(value = "configurations/{name}/update", method = RequestMethod.PUT)
    public ArtifactoryConfiguration updateConfiguration(@PathVariable String name, @RequestBody ArtifactoryConfiguration configuration) {
        configurationService.updateConfiguration(name, configuration);
        return getConfiguration(name);
    }

}
