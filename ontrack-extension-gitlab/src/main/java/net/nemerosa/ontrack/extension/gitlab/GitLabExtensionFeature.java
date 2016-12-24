package net.nemerosa.ontrack.extension.gitlab;

import net.nemerosa.ontrack.extension.git.GitExtensionFeature;
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitLabExtensionFeature extends AbstractExtensionFeature {

    @Autowired
    public GitLabExtensionFeature(GitExtensionFeature gitExtensionFeature) {
        super("gitlab", "GitLab", "Support for GitLab", ExtensionFeatureOptions.DEFAULT
                .withGui(true)
                .withDependency(gitExtensionFeature)
        );
    }
}
