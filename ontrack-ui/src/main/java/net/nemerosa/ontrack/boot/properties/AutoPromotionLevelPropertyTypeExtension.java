package net.nemerosa.ontrack.boot.properties;

import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutoPromotionLevelPropertyTypeExtension extends AbstractPropertyTypeExtension<AutoPromotionLevelProperty> {

    @Autowired
    public AutoPromotionLevelPropertyTypeExtension(CoreExtensionFeature extensionFeature) {
        super(extensionFeature, new AutoPromotionLevelPropertyType());
    }

}
