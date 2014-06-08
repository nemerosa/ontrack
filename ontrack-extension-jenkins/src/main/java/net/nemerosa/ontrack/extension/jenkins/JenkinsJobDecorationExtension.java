package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

/**
 * Provides a decoration that displays the state of a running job.
 */
@Component
public class JenkinsJobDecorationExtension extends AbstractExtension implements DecorationExtension {

    private final PropertyService propertyService;

    @Autowired
    public JenkinsJobDecorationExtension(JenkinsExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.of(
                ProjectEntityType.PROJECT,
                ProjectEntityType.BRANCH,
                ProjectEntityType.PROMOTION_LEVEL,
                ProjectEntityType.VALIDATION_STAMP
        );
    }

    @Override
    public Decoration getDecoration(ProjectEntity entity) {
        // Gets the Jenkins Job property for this entity, if any
        Property<JenkinsJobProperty> property = propertyService.getProperty(entity, JenkinsJobPropertyType.class.getName());
        if (property.isEmpty()) {
            return null;
        } else {
            // TODO Gets the state of the decoration
            return Decoration.of(
                    this,
                    "idle",
                    "The Jenkins job is not running."
            );
        }
    }
}
