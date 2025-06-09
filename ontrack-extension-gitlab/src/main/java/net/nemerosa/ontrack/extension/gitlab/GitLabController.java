package net.nemerosa.ontrack.extension.gitlab;

import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration;
import net.nemerosa.ontrack.extension.gitlab.service.GitLabConfigurationService;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;
import net.nemerosa.ontrack.model.support.ConfigurationDescriptor;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("extension/gitlab")
public class GitLabController extends AbstractExtensionController<GitLabExtensionFeature> {

    private final GitLabConfigurationService configurationService;

    @Autowired
    public GitLabController(GitLabExtensionFeature feature,
                            GitLabConfigurationService configurationService) {
        super(feature);
        this.configurationService = configurationService;
    }

    @Override
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ExtensionFeatureDescription getDescription() {
        return feature.getFeatureDescription();
    }

    /**
     * Gets the configurations
     */
    @RequestMapping(value = "configurations", method = RequestMethod.GET)
    public List<GitLabConfiguration> getConfigurations() {
        return configurationService.getConfigurations();
    }

    /**
     * Test for a configuration
     */
    @RequestMapping(value = "configurations/test", method = RequestMethod.POST)
    public ConnectionResult testConfiguration(@RequestBody GitLabConfiguration configuration) {
        return configurationService.test(configuration);
    }

    /**
     * Gets the configuration descriptors
     */
    @RequestMapping(value = "configurations/descriptors", method = RequestMethod.GET)
    public List<ConfigurationDescriptor> getConfigurationsDescriptors() {
        return configurationService.getConfigurationDescriptors();
    }

    /**
     * Creating a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.POST)
    public GitLabConfiguration newConfiguration(@RequestBody GitLabConfiguration configuration) {
        return configurationService.newConfiguration(configuration);
    }

    /**
     * Gets one configuration
     */
    @RequestMapping(value = "configurations/{name:.*}", method = RequestMethod.GET)
    public GitLabConfiguration getConfiguration(@PathVariable String name) {
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
    public GitLabConfiguration updateConfiguration(@PathVariable String name, @RequestBody GitLabConfiguration configuration) {
        configurationService.updateConfiguration(name, configuration);
        return getConfiguration(name);
    }
}
