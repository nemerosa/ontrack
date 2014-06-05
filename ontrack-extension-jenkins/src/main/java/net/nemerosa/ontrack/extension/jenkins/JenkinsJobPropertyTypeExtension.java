package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import net.nemerosa.ontrack.model.structure.PropertyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JenkinsJobPropertyTypeExtension extends AbstractPropertyTypeExtension<JenkinsJobProperty> {

    @Autowired
    public JenkinsJobPropertyTypeExtension(JenkinsExtensionFeature extensionFeature) {
        super(extensionFeature, new JenkinsJobPropertyType());
    }
}
