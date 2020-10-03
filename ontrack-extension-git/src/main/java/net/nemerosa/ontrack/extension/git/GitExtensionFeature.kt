package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature;
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitExtensionFeature extends AbstractExtensionFeature {

    @Autowired
    public GitExtensionFeature(SCMExtensionFeature scmExtensionFeature) {
        super("git", "Git", "Support for Git", ExtensionFeatureOptions.DEFAULT
                        .withGui(true)
                        .withDependency(scmExtensionFeature)
        );
    }
}
