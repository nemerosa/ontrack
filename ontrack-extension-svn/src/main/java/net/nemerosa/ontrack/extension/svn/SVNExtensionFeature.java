package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.stereotype.Component;

@Component
public class SVNExtensionFeature extends AbstractExtensionFeature {
    public SVNExtensionFeature() {
        super("svn", "Subversion", "Support for Subversion", ExtensionFeatureOptions.DEFAULT.withGui(true));
    }
}
