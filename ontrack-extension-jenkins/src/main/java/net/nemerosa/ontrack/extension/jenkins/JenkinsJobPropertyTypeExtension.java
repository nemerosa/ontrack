package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JenkinsJobPropertyTypeExtension extends AbstractPropertyTypeExtension<JenkinsJobProperty> {

    private final JenkinsConfigurationService configurationService;

    @Autowired
    public JenkinsJobPropertyTypeExtension(JenkinsExtensionFeature extensionFeature, JenkinsConfigurationService configurationService) {
        super(extensionFeature, new JenkinsJobPropertyType(configurationService));
        this.configurationService = configurationService;
    }
}
