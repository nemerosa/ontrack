package net.nemerosa.ontrack.boot.properties;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import org.springframework.stereotype.Component;

@Component
public class CoreExtensionFeature extends AbstractExtensionFeature {

    public CoreExtensionFeature() {
        super("core", "Core", "Core extensions");
    }

}
