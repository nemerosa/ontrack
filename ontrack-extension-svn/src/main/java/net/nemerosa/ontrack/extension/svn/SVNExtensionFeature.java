package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import org.springframework.stereotype.Component;

@Component
public class SVNExtensionFeature extends AbstractExtensionFeature {
    public SVNExtensionFeature() {
        super("svn", "Subversion", "Support for Subversion");
    }
}
