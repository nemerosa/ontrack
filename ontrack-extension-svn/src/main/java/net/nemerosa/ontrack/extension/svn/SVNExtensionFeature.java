package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature;
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SVNExtensionFeature extends AbstractExtensionFeature {

    @Autowired
    public SVNExtensionFeature(SCMExtensionFeature scmExtensionFeature) {
        super("svn", "Subversion", "Support for Subversion", ExtensionFeatureOptions.DEFAULT
                        .withGui(true)
                        .withDependency(scmExtensionFeature)
        );
    }
}
