package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetaInfoPropertyTypeExtension extends AbstractPropertyTypeExtension<MetaInfoProperty> {

    @Autowired
    public MetaInfoPropertyTypeExtension(GeneralExtensionFeature extensionFeature) {
        super(extensionFeature, new MetaInfoPropertyType());
    }

}
