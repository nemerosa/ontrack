package net.nemerosa.ontrack.extension.scm;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.stereotype.Component;

/**
 * Extension feature for anything related to the SCM
 */
@Component
public class SCMExtensionFeature extends AbstractExtensionFeature {

    public SCMExtensionFeature() {
        super("scm", "SCM", "SCM common code", ExtensionFeatureOptions.DEFAULT.withGui(true));
    }
}
