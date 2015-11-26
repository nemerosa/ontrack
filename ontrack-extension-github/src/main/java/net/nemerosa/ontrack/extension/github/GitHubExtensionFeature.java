package net.nemerosa.ontrack.extension.github;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.stereotype.Component;

@Component
public class GitHubExtensionFeature extends AbstractExtensionFeature {

    public GitHubExtensionFeature() {
        super("github", "GitHub", "Support for GitHub", ExtensionFeatureOptions.DEFAULT.withGui(true));
    }
}
