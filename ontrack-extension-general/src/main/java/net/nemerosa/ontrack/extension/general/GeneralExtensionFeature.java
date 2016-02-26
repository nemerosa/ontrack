package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.stereotype.Component;

@Component
public class GeneralExtensionFeature extends AbstractExtensionFeature {

    public GeneralExtensionFeature() {
        super("general", "General", "Core extensions", ExtensionFeatureOptions.DEFAULT.withGui(true));
    }

}
