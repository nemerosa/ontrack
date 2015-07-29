package net.nemerosa.ontrack.extension.stash.property;

import net.nemerosa.ontrack.extension.stash.StashExtensionFeature;
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService;
import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StashProjectConfigurationPropertyTypeExtension extends AbstractPropertyTypeExtension<StashProjectConfigurationProperty> {

    @Autowired
    public StashProjectConfigurationPropertyTypeExtension(StashExtensionFeature extensionFeature, StashConfigurationService configurationService) {
        super(extensionFeature, new StashProjectConfigurationPropertyType(configurationService));
    }
}
