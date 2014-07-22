package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.stereotype.Component;

@Component
public class ReleasePropertyTypeExtension extends AbstractPropertyTypeExtension<ReleaseProperty> {

    public ReleasePropertyTypeExtension(GeneralExtensionFeature extensionFeature) {
        super(extensionFeature, new ReleasePropertyType());
    }

}
