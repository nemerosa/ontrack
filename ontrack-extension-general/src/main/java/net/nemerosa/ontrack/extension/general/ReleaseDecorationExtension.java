package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.*;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class ReleaseDecorationExtension extends AbstractExtension implements DecorationExtension {

    private final PropertyService propertyService;

    @Autowired
    public ReleaseDecorationExtension(GeneralExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.of(ProjectEntityType.BUILD);
    }

    @Override
    public Decoration getDecoration(ProjectEntity entity) {
        // Argument check
        Validate.isTrue(entity instanceof Build, "Expecting build");
        // Gets the `release` property
        Property<ReleaseProperty> property = propertyService.getProperty(entity, ReleasePropertyType.class);
        if (property.isEmpty()) {
            return null;
        } else {
            return Decoration.of(
                    this,
                    "release",
                    property.getValue().getName()
            ).withName(property.getValue().getName());
        }
    }

}
