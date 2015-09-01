package net.nemerosa.ontrack.extension.stash.property;

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StashProjectConfigurationPropertyCleanupListener extends AbstractConfigurationPropertyCleanupListener<StashConfiguration, StashProjectConfigurationProperty> {

    @Autowired
    public StashProjectConfigurationPropertyCleanupListener(PropertyService propertyService, StructureService structureService) {
        super(propertyService, structureService, StashProjectConfigurationPropertyType.class);
    }
}
