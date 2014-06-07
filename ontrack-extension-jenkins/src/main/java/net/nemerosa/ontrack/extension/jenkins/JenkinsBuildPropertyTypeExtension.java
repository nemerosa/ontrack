package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JenkinsBuildPropertyTypeExtension extends AbstractPropertyTypeExtension<JenkinsBuildProperty> {

    @Autowired
    public JenkinsBuildPropertyTypeExtension(JenkinsExtensionFeature extensionFeature, JenkinsConfigurationService configurationService) {
        super(extensionFeature, new JenkinsBuildPropertyType(configurationService));
    }
}
