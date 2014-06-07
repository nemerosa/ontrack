package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import org.springframework.stereotype.Component;

@Component
public class JIRAExtensionFeature extends AbstractExtensionFeature {

    public JIRAExtensionFeature() {
        super("jira", "JIRA", "Provides links with JIRA");
    }

}
