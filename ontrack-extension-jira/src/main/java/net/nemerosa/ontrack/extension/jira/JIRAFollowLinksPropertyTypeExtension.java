package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JIRAFollowLinksPropertyTypeExtension extends AbstractPropertyTypeExtension<JIRAFollowLinksProperty> {

    @Autowired
    public JIRAFollowLinksPropertyTypeExtension(JIRAExtensionFeature extensionFeature) {
        super(extensionFeature, new JIRAFollowLinksPropertyType());
    }

}
