package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Component
public class AutoPromotionPropertyDecorator extends AbstractExtension
        implements DecorationExtension<Boolean> {

    private final PropertyService propertyService;

    @Autowired
    public AutoPromotionPropertyDecorator(GeneralExtensionFeature extensionFeature, PropertyService propertyService) {
        super(extensionFeature);
        this.propertyService = propertyService;
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.of(ProjectEntityType.PROMOTION_LEVEL);
    }

    @Override
    public List<Decoration<Boolean>> getDecorations(ProjectEntity entity) {
        return propertyService.getProperty(entity, AutoPromotionPropertyType.class).option()
                .map(autoPromotionProperty -> Collections.singletonList(Decoration.of(this, Boolean.TRUE)))
                .orElse(Collections.emptyList());
    }
}
