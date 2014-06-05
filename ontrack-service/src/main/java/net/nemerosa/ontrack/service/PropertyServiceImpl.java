package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.PropertyTypeExtension;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.PropertyRepository;
import net.nemerosa.ontrack.repository.TProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final SecurityService securityService;
    private final ExtensionManager extensionManager;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepository, SecurityService securityService, ExtensionManager extensionManager) {
        this.propertyRepository = propertyRepository;
        this.securityService = securityService;
        this.extensionManager = extensionManager;
    }

    @Override
    public List<PropertyType<?>> getPropertyTypes() {
        Collection<PropertyTypeExtension> extensions = extensionManager.getExtensions(PropertyTypeExtension.class);
        List<PropertyType<?>> propertyTypes = new ArrayList<>();
        for (PropertyTypeExtension extension : extensions) {
            propertyTypes.add(
                    extension.getPropertyType()
            );
        }
        return propertyTypes;
    }

    @Override
    public List<Property<?>> getProperties(ProjectEntity entity) {
        // With all the existing properties...
        return getPropertyTypes().stream()
                // ... filters them by entity
                .filter(type -> type.applies(entity.getClass()))
                        // ... filters them by access right
                .filter(type -> type.canView(entity, securityService))
                        // ... loads them from the store
                .map(type -> loadProperty(type, entity))
                        // ... removes the null values
                .filter(prop -> prop != null)
                        // ... and returns them
                .collect(Collectors.toList());
    }

    protected <T> Property<T> loadProperty(PropertyType<T> type, ProjectEntity entity) {
        // Gets the raw information from the repository
        TProperty t = propertyRepository.loadProperty(
                type.getClass().getName(),
                entity.getProjectEntityType(),
                entity.getId());
        // If null, returns null
        if (t == null) {
            return null;
        }
        // Converts the stored value into an actual value
        T value = type.fromStorage(t.getJson());
        // OK
        return Property.of(type, value);
    }

    @Override
    public List<PropertyTypeDescriptor> getEditableProperties(ProjectEntity entity) {
        //noinspection Convert2MethodRef
        return getPropertyTypes().stream()
                .filter(p -> p.applies(entity.getClass()))
                .filter(p -> p.canEdit(entity, securityService))
                .map(p -> PropertyTypeDescriptor.of(p))
                .collect(Collectors.toList());
    }
}
