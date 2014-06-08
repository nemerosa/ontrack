package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import org.springframework.stereotype.Component;

@Component
public class GeneralExtensionFeature extends AbstractExtensionFeature {

    public GeneralExtensionFeature() {
        super("genaral", "General", "Core extensions");
    }

}
