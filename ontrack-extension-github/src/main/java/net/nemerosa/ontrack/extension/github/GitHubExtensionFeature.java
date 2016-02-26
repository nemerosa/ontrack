package net.nemerosa.ontrack.extension.github;

import net.nemerosa.ontrack.extension.git.GitExtensionFeature;
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitHubExtensionFeature extends AbstractExtensionFeature {

    @Autowired
    public GitHubExtensionFeature(GitExtensionFeature gitExtensionFeature) {
        super("github", "GitHub", "Support for GitHub", ExtensionFeatureOptions.DEFAULT
                        .withGui(true)
                        .withDependency(gitExtensionFeature)
        );
    }
}
