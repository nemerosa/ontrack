package net.nemerosa.ontrack.extension.stash;

import net.nemerosa.ontrack.extension.git.GitExtensionFeature;
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StashExtensionFeature extends AbstractExtensionFeature {

    @Autowired
    public StashExtensionFeature(GitExtensionFeature gitExtensionFeature) {
        super("stash", "Bitbucket", "Support for Atlassian Bitbucket (Cloud & Server)",
                ExtensionFeatureOptions.DEFAULT
                        .withGui(true)
                        .withDependency(gitExtensionFeature)
        );
    }
}
