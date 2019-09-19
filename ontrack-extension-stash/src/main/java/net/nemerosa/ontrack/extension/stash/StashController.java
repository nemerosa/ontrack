package net.nemerosa.ontrack.extension.stash;

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration;
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("extension/stash")
public class StashController extends AbstractExtensionController<StashExtensionFeature> {

    private final StashConfigurationService configurationService;
    private final SecurityService securityService;

    @Autowired
    public StashController(StashExtensionFeature feature,
                           StashConfigurationService configurationService,
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
                uri(on(getClass()).getDescription())
        )
                .with("configurations", uri(on(getClass()).getConfigurations()), securityService.isGlobalFunctionGranted(GlobalSettings.class))
                ;
    }

    /**
     * Gets the configurations
     */
    @RequestMapping(value = "configurations", method = RequestMethod.GET)
    public Resources<StashConfiguration> getConfigurations() {
        return Resources.of(
                configurationService.getConfigurations(),
                uri(on(getClass()).getConfigurations())
        )
                .with(Link.CREATE, uri(on(getClass()).getConfigurationForm()))
                .with("_test", uri(on(getClass()).testConfiguration(null)), securityService.isGlobalFunctionGranted(GlobalSettings.class))
                ;
    }

    /**
     * Test for a configuration
     */
    @RequestMapping(value = "configurations/test", method = RequestMethod.POST)
    public ConnectionResult testConfiguration(@RequestBody StashConfiguration configuration) {
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
     * Form for a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.GET)
    public Form getConfigurationForm() {
        return StashConfiguration.Companion.form();
    }

    /**
     * Creating a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.POST)
    public StashConfiguration newConfiguration(@RequestBody StashConfiguration configuration) {
        return configurationService.newConfiguration(configuration);
    }

    /**
     * Gets one configuration
     */
    @RequestMapping(value = "configurations/{name:.*}", method = RequestMethod.GET)
    public StashConfiguration getConfiguration(@PathVariable String name) {
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
     * Update form
     */
    @RequestMapping(value = "configurations/{name:.*}/update", method = RequestMethod.GET)
    public Form updateConfigurationForm(@PathVariable String name) {
        return configurationService.getConfiguration(name).asForm();
    }

    /**
     * Updating one configuration
     */
    @RequestMapping(value = "configurations/{name:.*}/update", method = RequestMethod.PUT)
    public StashConfiguration updateConfiguration(@PathVariable String name, @RequestBody StashConfiguration configuration) {
        configurationService.updateConfiguration(name, configuration);
        return getConfiguration(name);
    }
}
