package net.nemerosa.ontrack.extension.github;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import org.springframework.stereotype.Component;

@Component
public class GitHubExtensionFeature extends AbstractExtensionFeature {

    public GitHubExtensionFeature() {
        super("github", "GitHub", "Support for GitHub");
    }
}
