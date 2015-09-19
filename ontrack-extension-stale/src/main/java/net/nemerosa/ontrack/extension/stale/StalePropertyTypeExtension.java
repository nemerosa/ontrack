package net.nemerosa.ontrack.extension.stale;

import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StalePropertyTypeExtension extends AbstractPropertyTypeExtension<StaleProperty> {

    @Autowired
    public StalePropertyTypeExtension(StaleExtensionFeature extensionFeature) {
        super(extensionFeature, new StalePropertyType());
    }

}
