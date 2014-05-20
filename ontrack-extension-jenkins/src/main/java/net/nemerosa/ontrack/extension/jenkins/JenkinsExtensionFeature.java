package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import org.springframework.stereotype.Component;

@Component
public class JenkinsExtensionFeature extends AbstractExtensionFeature {

    public JenkinsExtensionFeature() {
        super("jenkins", "Jenkins", "Provides links with Jenkins");
    }

}
