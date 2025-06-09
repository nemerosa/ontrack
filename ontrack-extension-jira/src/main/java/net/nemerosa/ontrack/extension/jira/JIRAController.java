package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory;
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

@RequestMapping("extension/jira")
@RestController
public class JIRAController extends AbstractExtensionController<JIRAExtensionFeature> {

    private final JIRAConfigurationService jiraConfigurationService;
    private final JIRASessionFactory jiraSessionFactory;
    private final SecurityService securityService;

    @Autowired
    public JIRAController(JIRAExtensionFeature feature, JIRAConfigurationService jiraConfigurationService, JIRASessionFactory jiraSessionFactory, SecurityService securityService) {
        super(feature);
        this.jiraConfigurationService = jiraConfigurationService;
        this.jiraSessionFactory = jiraSessionFactory;
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
     * Gets the JIRA settings
     */
    @RequestMapping(value = "configurations", method = RequestMethod.GET)
    public Resources<JIRAConfiguration> getConfigurations() {
        return Resources.of(
                        jiraConfigurationService.getConfigurations(),
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
                jiraConfigurationService.getConfigurationDescriptors(),
                uri(on(getClass()).getConfigurationsDescriptors())
        );
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
