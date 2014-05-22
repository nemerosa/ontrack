package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.jenkins.model.JenkinsConfiguration;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsService;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.ResourceCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

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
    @RequestMapping(value = "configurations", method = RequestMethod.GET)
    public ResourceCollection<JenkinsConfiguration> getConfigurations() {
        return ResourceCollection.of(
                jenkinsService.getConfigurations()
                        .stream()
                        .map(this::toConfigurationResource)
                        .collect(Collectors.toList()),
                uri(on(getClass()).getConfigurations())
        )
                .with("createConfiguration", uri(on(getClass()).getConfigurationForm()))
                ;
    }

    /**
     * Form for a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.GET)
    public Form getConfigurationForm() {
        return JenkinsConfiguration.form();
    }

    /**
     * Creating a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.POST)
    public Resource<JenkinsConfiguration> newConfiguration(@RequestBody JenkinsConfiguration configuration) {
        return toConfigurationResource(jenkinsService.newConfiguration(configuration));
    }

    /**
     * Gets one configuration
     */
    @RequestMapping(value = "configurations/{name}", method = RequestMethod.GET)
    public Resource<JenkinsConfiguration> getConfiguration(@PathVariable String name) {
        return toConfigurationResource(jenkinsService.getConfiguration(name));
    }

    /**
     * Deleting one configuration
     */
    @RequestMapping(value = "configurations/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.GONE)
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
    public Resource<JenkinsConfiguration> updateConfiguration(@PathVariable String name, @RequestBody JenkinsConfiguration configuration) {
        jenkinsService.updateConfiguration(name, configuration);
        return getConfiguration(name);
    }

    // Resource assemblers

    private Resource<JenkinsConfiguration> toConfigurationResource(JenkinsConfiguration configuration) {
        return Resource.of(
                configuration.obfuscate(),
                uri(on(getClass()).getConfiguration(configuration.getName()))
        )
                .with(Link.UPDATE, uri(on(getClass()).updateConfigurationForm(configuration.getName())))
                .with(Link.DELETE, uri(on(getClass()).deleteConfiguration(configuration.getName())))
                ;
    }

}
