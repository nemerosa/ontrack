package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutoPromotionPropertyTypeExtension extends AbstractPropertyTypeExtension<AutoPromotionProperty> {

    @Autowired
    public AutoPromotionPropertyTypeExtension(GeneralExtensionFeature extensionFeature, StructureService structureService) {
        super(extensionFeature, new AutoPromotionPropertyType(structureService));
    }
}
