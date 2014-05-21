package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.jenkins.model.JenkinsConfiguration;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsService;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsSettings;
import net.nemerosa.ontrack.extension.support.AbstractExtensionController;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}
