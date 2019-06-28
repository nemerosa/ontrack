package net.nemerosa.ontrack.extension.jenkins;

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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RequestMapping("extension/jenkins")
@RestController
public class JenkinsController extends AbstractExtensionController<JenkinsExtensionFeature> {

    private final JenkinsConfigurationService jenkinsService;
    private final SecurityService securityService;

    @Autowired
    public JenkinsController(JenkinsExtensionFeature feature, JenkinsConfigurationService jenkinsService, SecurityService securityService) {
        super(feature);
        this.jenkinsService = jenkinsService;
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
    public Resources<JenkinsConfiguration> getConfigurations() {
        return Resources.of(
                jenkinsService.getConfigurations(),
                uri(on(getClass()).getConfigurations())
        )
                .with(Link.CREATE, uri(on(getClass()).getConfigurationForm()))
                .with("_test", uri(on(getClass()).testConfiguration(null)), securityService.isGlobalFunctionGranted(GlobalSettings.class))
                ;
    }

    /**
     * Gets the configuration descriptors
     */
    @RequestMapping(value = "configurations/descriptors", method = RequestMethod.GET)
    public Resources<ConfigurationDescriptor> getConfigurationsDescriptors() {
        return Resources.of(
                jenkinsService.getConfigurationDescriptors(),
                uri(on(getClass()).getConfigurationsDescriptors())
        );
    }

    /**
     * Test for a configuration
     */
    @RequestMapping(value = "configurations/test", method = RequestMethod.POST)
    public ConnectionResult testConfiguration(@RequestBody JenkinsConfiguration configuration) {
        return jenkinsService.test(configuration);
    }

    /**
     * Form for a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.GET)
    public Form getConfigurationForm() {
        return JenkinsConfiguration.Companion.form();
    }

    /**
     * Creating a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.POST)
    public JenkinsConfiguration newConfiguration(@RequestBody JenkinsConfiguration configuration) {
        return jenkinsService.newConfiguration(configuration);
    }

    /**
     * Gets one configuration
     */
    @RequestMapping(value = "configurations/{name}", method = RequestMethod.GET)
    public JenkinsConfiguration getConfiguration(@PathVariable String name) {
        return jenkinsService.getConfiguration(name);
    }

    /**
     * Deleting one configuration
     */
    @RequestMapping(value = "configurations/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Ack deleteConfiguration(@PathVariable String name) {
        jenkinsService.deleteConfiguration(name);
        return Ack.OK;
    }

    /**
     * Update form
     */
    @RequestMapping(value = "configurations/{name}/update", method = RequestMethod.GET)
    public Form updateConfigurationForm(@PathVariable String name) {
        return jenkinsService.getConfiguration(name).asForm();
    }

    /**
     * Updating one configuration
     */
    @RequestMapping(value = "configurations/{name}/update", method = RequestMethod.PUT)
    public JenkinsConfiguration updateConfiguration(@PathVariable String name, @RequestBody JenkinsConfiguration configuration) {
        jenkinsService.updateConfiguration(name, configuration);
        return getConfiguration(name);
    }

}
