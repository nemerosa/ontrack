package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.BuildDiffExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationPropertyType;
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType;
import net.nemerosa.ontrack.model.support.Action;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SVNChangeLogExtension extends AbstractExtension implements BuildDiffExtension {

    private final PropertyService propertyService;

    @Autowired
    public SVNChangeLogExtension(SVNExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public Action getAction() {
        return Action.of("svn-changelog", "Change log", "changelog");
    }

    /**
     * Checks that the branch is properly configured with a SVN configuration.
     */
    @Override
    public boolean apply(Branch branch) {
        return !propertyService.getProperty(branch, SVNBranchConfigurationPropertyType.class).isEmpty()
                && !propertyService.getProperty(branch.getProject(), SVNProjectConfigurationPropertyType.class).isEmpty();
    }
}
