package net.nemerosa.ontrack.extension.stash.property;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventListener;
import net.nemerosa.ontrack.model.events.EventType;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StashProjectConfigurationPropertyCleanupListener implements EventListener {

    private final PropertyService propertyService;
    private final StructureService structureService;

    @Autowired
    public StashProjectConfigurationPropertyCleanupListener(PropertyService propertyService, StructureService structureService) {
        this.propertyService = propertyService;
        this.structureService = structureService;
    }

    @Override
    public void onEvent(Event event) {
        // For configuration being deleted
        EventType eventType = event.getEventType();
        if (eventType == EventFactory.DELETE_CONFIGURATION) {
            String configurationName = event.getValue("configuration");
            cleanup(configurationName);
        }
    }

    private void cleanup(String configurationName) {
        propertyService
                // Looks for all entities which have a property pointing to this configuration
                .searchWithPropertyValue(
                        StashProjectConfigurationPropertyType.class,
                        (entityType, id) -> entityType.getEntityFn(structureService).apply(id),
                        property -> StringUtils.equals(property.getConfiguration().getName(), configurationName)
                )
                        // Deleting this property
                .forEach(projectEntity -> propertyService.deleteProperty(projectEntity, StashProjectConfigurationPropertyType.class));
    }
}
