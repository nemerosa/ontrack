package net.nemerosa.ontrack.extension.stash;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.stereotype.Component;

@Component
public class StashExtensionFeature extends AbstractExtensionFeature {
    public StashExtensionFeature() {
        super("stash", "Stash", "Support for Atlassian Stash",
                ExtensionFeatureOptions.DEFAULT.withGui(true));
    }
}
