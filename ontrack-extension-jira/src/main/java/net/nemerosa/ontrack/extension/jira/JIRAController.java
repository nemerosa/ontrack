package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("extension/jira")
@RestController
public class JIRAController extends AbstractExtensionController<JIRAExtensionFeature> {

    private final JIRAConfigurationService jiraConfigurationService;

    @Autowired
    public JIRAController(JIRAExtensionFeature feature, JIRAConfigurationService jiraConfigurationService) {
        super(feature);
        this.jiraConfigurationService = jiraConfigurationService;
    }

    @Override
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ExtensionFeatureDescription getDescription() {
        return feature.getFeatureDescription();
    }

    /**
     * Gets the JIRA settings
     */
    @RequestMapping(value = "configurations", method = RequestMethod.GET)
    public List<JIRAConfiguration> getConfigurations() {
        return jiraConfigurationService.getConfigurations();
    }

    /**
     * Test for a configuration
     */
    @RequestMapping(value = "configurations/test", method = RequestMethod.POST)
    public ConnectionResult testConfiguration(@RequestBody JIRAConfiguration configuration) {
        return jiraConfigurationService.test(configuration);
    }

    /**
     * Creating a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.POST)
    public JIRAConfiguration newConfiguration(@RequestBody JIRAConfiguration configuration) {
        return jiraConfigurationService.newConfiguration(configuration);
    }

    /**
     * Gets one configuration
     */
    @RequestMapping(value = "configurations/{name}", method = RequestMethod.GET)
    public JIRAConfiguration getConfiguration(@PathVariable String name) {
        return jiraConfigurationService.getConfiguration(name);
    }

    /**
     * Deleting one configuration
     */
    @RequestMapping(value = "configurations/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Ack deleteConfiguration(@PathVariable String name) {
        jiraConfigurationService.deleteConfiguration(name);
        return Ack.OK;
    }

    /**
     * Updating one configuration
     */
    @RequestMapping(value = "configurations/{name}/update", method = RequestMethod.PUT)
    public JIRAConfiguration updateConfiguration(@PathVariable String name, @RequestBody JIRAConfiguration configuration) {
        jiraConfigurationService.updateConfiguration(name, configuration);
        return getConfiguration(name);
    }

}
