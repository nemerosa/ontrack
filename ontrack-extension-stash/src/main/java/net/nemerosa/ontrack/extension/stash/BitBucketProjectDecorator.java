package net.nemerosa.ontrack.extension.stash;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Component
public class BitBucketProjectDecorator extends AbstractExtension implements DecorationExtension<String> {

    private final PropertyService propertyService;

    @Autowired
    public BitBucketProjectDecorator(StashExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public List<Decoration<String>> getDecorations(ProjectEntity entity) {
        Property<StashProjectConfigurationProperty> property = propertyService.getProperty(entity, StashProjectConfigurationPropertyType.class);
        if (!property.isEmpty()) {
            return Collections.singletonList(
                    Decoration.of(
                            this,
                            String.format(
                                    "%s/%s @ %s",
                                    property.getValue().getProject(),
                                    property.getValue().getRepository(),
                                    property.getValue().getConfiguration().getName()
                            )
                    )
            );
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.of(ProjectEntityType.PROJECT);
    }
}
