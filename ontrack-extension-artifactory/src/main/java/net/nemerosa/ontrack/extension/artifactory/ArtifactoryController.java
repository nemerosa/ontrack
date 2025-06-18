package net.nemerosa.ontrack.extension.artifactory;

import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfigurationService;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("extension/artifactory")
@RestController
public class ArtifactoryController extends AbstractExtensionController<ArtifactoryExtensionFeature> {

    private final ArtifactoryConfigurationService configurationService;

    @Autowired
    public ArtifactoryController(ArtifactoryExtensionFeature feature, ArtifactoryConfigurationService configurationService) {
        super(feature);
        this.configurationService = configurationService;
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
    public List<ArtifactoryConfiguration> getConfigurations() {
        return configurationService.getConfigurations();
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
