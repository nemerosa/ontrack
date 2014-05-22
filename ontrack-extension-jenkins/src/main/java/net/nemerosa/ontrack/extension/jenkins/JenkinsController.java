package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.jenkins.model.JenkinsConfiguration;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsService;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsSettings;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RequestMapping("extension/jenkins")
@RestController
public class JenkinsController extends AbstractExtensionController<JenkinsExtensionFeature> {

    private final JenkinsService jenkinsService;

    @Autowired
    public JenkinsController(JenkinsExtensionFeature feature, JenkinsService jenkinsService) {
        super(feature);
        this.jenkinsService = jenkinsService;
    }

    /**
     * Gets the Jenkins settings
     */
    @RequestMapping(value = "settings", method = RequestMethod.GET)
    public Resource<JenkinsSettings> getSettings() {
        return Resource.of(
                jenkinsService.getSettings(),
                uri(on(getClass()).getSettings())
        )
                .with("createConfiguration", uri(on(getClass()).getConfigurationForm()))
                ;
    }

    /**
     * Form for a configuration
     */
    @RequestMapping(value = "settings/configuration/create", method = RequestMethod.GET)
    public Form getConfigurationForm() {
        return JenkinsConfiguration.form();
    }

    /**
     * Creating a configuration
     */
    @RequestMapping(value = "settings/configuration/create", method = RequestMethod.POST)
    public Resource<JenkinsConfiguration> newConfiguration(@RequestBody JenkinsConfiguration configuration) {
        return toConfigurationResource(jenkinsService.newConfiguration(configuration));
    }

    private Resource<JenkinsConfiguration> toConfigurationResource(JenkinsConfiguration configuration) {
        return Resource.of(
                configuration.obfuscate(),
                uri(on(getClass()).getConfiguration(configuration.getName()))
        );
    }

    /**
     * Gets one configuration
     */
    @RequestMapping(value = "settings/configuration/{name}", method = RequestMethod.GET)
    public Resource<JenkinsConfiguration> getConfiguration(@PathVariable String name) {
        return toConfigurationResource(jenkinsService.getConfiguration(name));
    }

    /**
     * Deleting one configuration
     */
    @RequestMapping(value = "settings/configuration/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.GONE)
    public void deleteConfiguration(@PathVariable String name) {
        jenkinsService.deleteConfiguration(name);
    }

    /**
     * Updating one configuration
     */
    @RequestMapping(value = "settings/configuration/{name}", method = RequestMethod.PUT)
    public Resource<JenkinsConfiguration> updateConfiguration(@PathVariable String name, @RequestBody JenkinsConfiguration configuration) {
        jenkinsService.updateConfiguration(name, configuration);
        return getConfiguration(name);
    }

}
