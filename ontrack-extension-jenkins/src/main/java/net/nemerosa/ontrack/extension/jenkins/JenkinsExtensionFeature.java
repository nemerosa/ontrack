package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.stereotype.Component;

@Component
public class JenkinsExtensionFeature extends AbstractExtensionFeature {

    public JenkinsExtensionFeature() {
        super("jenkins", "Jenkins", "Provides links with Jenkins", ExtensionFeatureOptions.DEFAULT.withGui(true));
    }

}
