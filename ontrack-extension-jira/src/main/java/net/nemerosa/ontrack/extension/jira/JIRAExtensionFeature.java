package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.stereotype.Component;

@Component
public class JIRAExtensionFeature extends AbstractExtensionFeature {

    public JIRAExtensionFeature() {
        super("jira", "JIRA", "Provides links with JIRA", ExtensionFeatureOptions.DEFAULT.withGui(true));
    }

}
