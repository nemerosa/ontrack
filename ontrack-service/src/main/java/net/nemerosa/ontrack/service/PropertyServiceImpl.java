package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.PropertyTypeExtension;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.PropertyTypeNotFoundException;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.Property;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.PropertyType;
import net.nemerosa.ontrack.repository.PropertyRepository;
import net.nemerosa.ontrack.repository.TProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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

    protected <T> PropertyType<T> getPropertyTypeByName(String propertyTypeName) {
        //noinspection unchecked
        return (PropertyType<T>) getPropertyTypes().stream()
                .filter(p -> StringUtils.equals(propertyTypeName, p.getClass().getName()))
                .findFirst()
                .orElseThrow(() -> new PropertyTypeNotFoundException(propertyTypeName));
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
                .map(type -> getProperty(type, entity))
                        // .. flags with editionrights
                .map(prop -> prop.editable(prop.getType().canEdit(entity, securityService)))
                        // ... and returns them
                .collect(Collectors.toList());
    }

    @Override
    public <T> Property<T> getProperty(ProjectEntity entity, String propertyTypeName) {
        // Gets the property using its fully qualified type name
        PropertyType<T> propertyType = getPropertyTypeByName(propertyTypeName);
        // Access
        return getProperty(propertyType, entity);
    }

    @Override
    public Ack editProperty(ProjectEntity entity, String propertyTypeName, JsonNode data) {
        // Gets the property using its fully qualified type name
        PropertyType<?> propertyType = getPropertyTypeByName(propertyTypeName);
        // Edits the property
        return editProperty(entity, propertyType, data);
    }

    private <T> Ack editProperty(ProjectEntity entity, PropertyType<T> propertyType, JsonNode data) {
        // Checks for edition
        if (!propertyType.canEdit(entity, securityService)) {
            throw new AccessDeniedException("Property is not opened for viewing.");
        }
        // Gets the value and validates it
        T value = propertyType.fromClient(data);
        // Gets the JSON for the storage
        JsonNode storage = propertyType.forStorage(value);
        // Stores the property
        propertyRepository.saveProperty(
                propertyType.getClass().getName(),
                entity.getProjectEntityType(),
                entity.getId(),
                storage
        );
        // OK
        return Ack.OK;
    }

    protected <T> Property<T> getProperty(PropertyType<T> type, ProjectEntity entity) {
        T value = getPropertyValue(type, entity);
        return value != null ? Property.of(type, value) : Property.empty(type);
    }

    protected <T> T getPropertyValue(PropertyType<T> type, ProjectEntity entity) {
        // Checks for viewing
        if (!type.canView(entity, securityService)) {
            throw new AccessDeniedException("Property is not opened for viewing.");
        }
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
        return type.fromStorage(t.getJson());
    }

    @Override
    public Form getPropertyEditionForm(ProjectEntity entity, String propertyTypeName) {
        // Gets the property using its fully qualified type name
        PropertyType<?> propertyType = getPropertyTypeByName(propertyTypeName);
        // Gets the edition form for this type
        return getPropertyEditionForm(entity, propertyType);
    }

    protected <T> Form getPropertyEditionForm(ProjectEntity entity, PropertyType<T> propertyType) {
        // Checks for edition
        if (!propertyType.canEdit(entity, securityService)) {
            throw new AccessDeniedException("Property is not opened for edition.");
        }
        // Gets the value for this property
        T value = getPropertyValue(propertyType, entity);
        // Gets the form
        return propertyType.getEditionForm(Optional.ofNullable(value));
    }
}
