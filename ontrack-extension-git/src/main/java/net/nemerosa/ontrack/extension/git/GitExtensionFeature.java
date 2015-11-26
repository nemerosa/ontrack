package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.stereotype.Component;

@Component
public class GitExtensionFeature extends AbstractExtensionFeature {

    public GitExtensionFeature() {
        super("git", "Git", "Support for Git", ExtensionFeatureOptions.DEFAULT.withGui(true));
    }
}
