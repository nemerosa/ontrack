package net.nemerosa.ontrack.model.support;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventListener;
import net.nemerosa.ontrack.model.events.EventType;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.PropertyType;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.apache.commons.lang3.StringUtils;

// TODO Global listener to scan all property types linked to configurations?
public abstract class AbstractConfigurationPropertyCleanupListener<C extends UserPasswordConfiguration<C>, T extends ConfigurationProperty<C>> implements EventListener {

    private final PropertyService propertyService;
    private final StructureService structureService;
    private final SecurityService securityService;
    private final Class<? extends PropertyType<T>> propertyType;

    public AbstractConfigurationPropertyCleanupListener(PropertyService propertyService, StructureService structureService, SecurityService securityService, Class<? extends PropertyType<T>> propertyType) {
        this.propertyService = propertyService;
        this.structureService = structureService;
        this.securityService = securityService;
        this.propertyType = propertyType;
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
        securityService.asAdmin(() ->
                        propertyService
                                // Looks for all entities which have a property pointing to this configuration
                                .searchWithPropertyValue(
                                        propertyType,
                                        (entityType, id) -> entityType.getEntityFn(structureService).apply(id),
                                        property -> StringUtils.equals(property.getConfiguration().getName(), configurationName)
                                )
                                        // Deleting this property
                                .forEach(projectEntity -> propertyService.deleteProperty(projectEntity, propertyType))
        );
    }
}
