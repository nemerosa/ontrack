package net.nemerosa.ontrack.extension.stash;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import org.springframework.stereotype.Component;

@Component
public class StashExtensionFeature extends AbstractExtensionFeature {
    public StashExtensionFeature() {
        super("stash", "BitBucket", "Support for Atlassian BitBucket (Cloud & Server)");
    }
}
