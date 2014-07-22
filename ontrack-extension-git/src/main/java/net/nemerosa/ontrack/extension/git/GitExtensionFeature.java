package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import org.springframework.stereotype.Component;

@Component
public class GitExtensionFeature extends AbstractExtensionFeature {

    public GitExtensionFeature() {
        super("git", "Git", "Support for Git");
    }
}
