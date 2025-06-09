package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("extension/jenkins")
@RestController
public class JenkinsController extends AbstractExtensionController<JenkinsExtensionFeature> {

    private final JenkinsConfigurationService jenkinsService;

    @Autowired
    public JenkinsController(JenkinsExtensionFeature feature, JenkinsConfigurationService jenkinsService) {
        super(feature);
        this.jenkinsService = jenkinsService;
    }

    @Override
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ExtensionFeatureDescription getDescription() {
        return feature.getFeatureDescription();
    }

    /**
     * Gets the Jenkins settings
     */
    @RequestMapping(value = "configurations", method = RequestMethod.GET)
    public List<JenkinsConfiguration> getConfigurations() {
        return jenkinsService.getConfigurations();
    }

    /**
     * Gets the configuration descriptors
     */
    @RequestMapping(value = "configurations/descriptors", method = RequestMethod.GET)
    public List<ConfigurationDescriptor> getConfigurationsDescriptors() {
        return jenkinsService.getConfigurationDescriptors();
    }

    /**
     * Test for a configuration
     */
    @RequestMapping(value = "configurations/test", method = RequestMethod.POST)
    public ConnectionResult testConfiguration(@RequestBody JenkinsConfiguration configuration) {
        return jenkinsService.test(configuration);
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
     * Updating one configuration
     */
    @RequestMapping(value = "configurations/{name}/update", method = RequestMethod.PUT)
    public JenkinsConfiguration updateConfiguration(@PathVariable String name, @RequestBody JenkinsConfiguration configuration) {
        jenkinsService.updateConfiguration(name, configuration);
        return getConfiguration(name);
    }

}
