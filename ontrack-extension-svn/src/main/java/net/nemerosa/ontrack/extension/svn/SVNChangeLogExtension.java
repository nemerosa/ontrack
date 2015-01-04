package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.BuildDiffExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.support.Action;
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
     * Checks that the project is properly configured with a SVN configuration.
     */
    @Override
    public boolean apply(Project project) {
        return !propertyService.getProperty(project, SVNProjectConfigurationPropertyType.class).isEmpty();
    }
}
