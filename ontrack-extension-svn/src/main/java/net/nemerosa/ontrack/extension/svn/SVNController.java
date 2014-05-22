package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.ExtensionFeatureDescription;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.ResourceCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("extension/svn")
public class SVNController extends AbstractExtensionController<SVNExtensionFeature> {

    private final SVNConfigurationService svnConfigurationService;
    private final SecurityService securityService;

    @Autowired
    public SVNController(SVNExtensionFeature feature, SVNConfigurationService svnConfigurationService, SecurityService securityService) {
        super(feature);
        this.svnConfigurationService = svnConfigurationService;
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
     * Gets the configurations
     */
    @RequestMapping(value = "configurations", method = RequestMethod.GET)
    public ResourceCollection<SVNConfiguration> getConfigurations() {
        return ResourceCollection.of(
                svnConfigurationService.getConfigurations()
                        .stream()
                        .map(this::toConfigurationResource)
                        .collect(Collectors.toList()),
                uri(on(getClass()).getConfigurations())
        )
                .with(Link.CREATE, uri(on(getClass()).getConfigurationForm()))
                ;
    }

    /**
     * Form for a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.GET)
    public Form getConfigurationForm() {
        return SVNConfiguration.form();
    }

    /**
     * Creating a configuration
     */
    @RequestMapping(value = "configurations/create", method = RequestMethod.POST)
    public Resource<SVNConfiguration> newConfiguration(@RequestBody SVNConfiguration configuration) {
        return toConfigurationResource(svnConfigurationService.newConfiguration(configuration));
    }

    /**
     * Gets one configuration
     */
    @RequestMapping(value = "configurations/{name}", method = RequestMethod.GET)
    public Resource<SVNConfiguration> getConfiguration(@PathVariable String name) {
        return toConfigurationResource(svnConfigurationService.getConfiguration(name));
    }

    /**
     * Deleting one configuration
     */
    @RequestMapping(value = "configurations/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Ack deleteConfiguration(@PathVariable String name) {
        svnConfigurationService.deleteConfiguration(name);
        return Ack.OK;
    }

    /**
     * Update form
     */
    @RequestMapping(value = "configurations/{name}/update", method = RequestMethod.GET)
    public Form updateConfigurationForm(@PathVariable String name) {
        return svnConfigurationService.getConfiguration(name).asForm();
    }

    /**
     * Updating one configuration
     */
    @RequestMapping(value = "configurations/{name}/update", method = RequestMethod.PUT)
    public Resource<SVNConfiguration> updateConfiguration(@PathVariable String name, @RequestBody SVNConfiguration configuration) {
        svnConfigurationService.updateConfiguration(name, configuration);
        return getConfiguration(name);
    }

    // Resource assemblers

    private Resource<SVNConfiguration> toConfigurationResource(SVNConfiguration configuration) {
        return Resource.of(
                configuration.obfuscate(),
                uri(on(getClass()).getConfiguration(configuration.getName()))
        )
                .with(Link.UPDATE, uri(on(getClass()).updateConfigurationForm(configuration.getName())))
                .with(Link.DELETE, uri(on(getClass()).deleteConfiguration(configuration.getName())))
                ;
    }

}
