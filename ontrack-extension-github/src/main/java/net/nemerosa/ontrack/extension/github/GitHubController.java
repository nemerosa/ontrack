package net.nemerosa.ontrack.extension.github;

import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("extension/github")
public class GitHubController extends AbstractExtensionController<GitHubExtensionFeature> {

    private final GitHubConfigurationService configurationService;

    @Autowired
    public GitHubController(GitHubExtensionFeature feature,
                            GitHubConfigurationService configurationService) {
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
    public List<GitHubEngineConfiguration> getConfigurations() {
        return configurationService.getConfigurations();
    }

    /**
     * Test for a configuration
     */
    @RequestMapping(value = "configurations/test", method = RequestMethod.POST)
    public ConnectionResult testConfiguration(@RequestBody GitHubEngineConfiguration configuration) {
        return configurationService.test(configuration);
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
