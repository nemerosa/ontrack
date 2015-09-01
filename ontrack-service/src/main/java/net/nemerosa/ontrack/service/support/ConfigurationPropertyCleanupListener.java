package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventListener;
import net.nemerosa.ontrack.model.events.EventType;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.PropertyType;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.model.support.ConfigurationProperty;
import net.nemerosa.ontrack.model.support.ConfigurationPropertyType;
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Global listener to clean properties linked to removed configurations.
 */
@Component
public class ConfigurationPropertyCleanupListener implements EventListener {

    private final PropertyService propertyService;
    private final StructureService structureService;
    private final SecurityService securityService;

    @Autowired
    public ConfigurationPropertyCleanupListener(PropertyService propertyService, StructureService structureService, SecurityService securityService) {
        this.propertyService = propertyService;
        this.structureService = structureService;
        this.securityService = securityService;
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
        securityService.asAdmin(() -> {
                    propertyService.getPropertyTypes().stream()
                            // Keeps only the types which are linked to configuration properties
                            .filter(propertyType -> propertyType instanceof ConfigurationPropertyType)
                                    // Casting
                            .map(propertyType -> (ConfigurationPropertyType) propertyType)
                                    // Cleanup for each type
                            .forEach(propertyType -> cleanupType(propertyType, configurationName));
                }
        );

    }

    private <C extends UserPasswordConfiguration<C>, T extends ConfigurationProperty<C>> void cleanupType(PropertyType<T> propertyType, String configurationName) {
        //noinspection unchecked
        propertyService
                // Looks for all entities which have a property pointing to this configuration
                .searchWithPropertyValue(
                        (Class<? extends PropertyType<T>>) propertyType.getClass(),
                        (entityType, id) -> entityType.getEntityFn(structureService).apply(id),
                        property -> StringUtils.equals(property.getConfiguration().getName(), configurationName)
                )
                        // Deleting this property
                .forEach(projectEntity -> propertyService.deleteProperty(projectEntity, propertyType.getTypeName()));
    }
}
