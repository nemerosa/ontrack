package net.nemerosa.ontrack.boot.properties;

import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutoValidationStampPropertyTypeExtension extends AbstractPropertyTypeExtension<AutoValidationStampProperty> {

    @Autowired
    public AutoValidationStampPropertyTypeExtension(CoreExtensionFeature extensionFeature) {
        super(extensionFeature, new AutoValidationStampPropertyType());
    }

}
