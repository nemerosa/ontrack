package net.nemerosa.ontrack.extension.stale;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import org.springframework.stereotype.Component;

@Component
public class StaleExtensionFeature extends AbstractExtensionFeature {
    public StaleExtensionFeature() {
        super("stale", "Stale branches", "Disabling and deleting stale branches");
    }
}
