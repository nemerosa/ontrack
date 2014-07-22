package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReleasePropertyTypeExtension extends AbstractPropertyTypeExtension<ReleaseProperty> {

    @Autowired
    public ReleasePropertyTypeExtension(GeneralExtensionFeature extensionFeature) {
        super(extensionFeature, new ReleasePropertyType());
    }

}
