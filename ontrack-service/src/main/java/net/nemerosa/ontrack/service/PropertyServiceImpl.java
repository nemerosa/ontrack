package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.exceptions.PropertyTypeNotFoundException;
import net.nemerosa.ontrack.model.exceptions.PropertyUnsupportedEntityTypeException;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.PropertyRepository;
import net.nemerosa.ontrack.repository.TProperty;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {

    private final EventPostService eventPostService;
    private final EventFactory eventFactory;
    private final PropertyRepository propertyRepository;
    private final SecurityService securityService;
    private final ExtensionManager extensionManager;

    /**
     * The number of available property types is fairly limited so a static cache is enough.
     */
    private final ConcurrentMap<String, PropertyType<?>> cache = new ConcurrentHashMap<>();

    @Autowired
    public PropertyServiceImpl(EventPostService eventPostService, EventFactory eventFactory, PropertyRepository propertyRepository, SecurityService securityService, ExtensionManager extensionManager) {
        this.eventPostService = eventPostService;
        this.eventFactory = eventFactory;
        this.propertyRepository = propertyRepository;
        this.securityService = securityService;
        this.extensionManager = extensionManager;
    }

    @Override
    public List<PropertyType<?>> getPropertyTypes() {
        Collection<PropertyType> types = extensionManager.getExtensions(PropertyType.class);
        List<PropertyType<?>> result = new ArrayList<>();
        types.forEach(result::add);
        return result;
    }

    @Override
    public <T> PropertyType<T> getPropertyTypeByName(String propertyTypeName) {
        @SuppressWarnings("unchecked")
        PropertyType<T> type = (PropertyType<T>) cache.computeIfAbsent(
                propertyTypeName,
                (ignored) -> getPropertyTypes().stream()
                        .filter(p -> StringUtils.equals(propertyTypeName, p.getClass().getName()))
                        .findFirst()
                        .orElse(null)
        );
        if (type != null) {
            return type;
        } else {
            throw new PropertyTypeNotFoundException(propertyTypeName);
        }
    }

    @Override
    public List<Property<?>> getProperties(ProjectEntity entity) {
        // With all the existing properties...
        return getPropertyTypes().stream()
                // ... filters them by entity
                .filter(type -> type.getSupportedEntityTypes().contains(entity.getProjectEntityType()))
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
    public <T> Property<T> getProperty(ProjectEntity entity, Class<? extends PropertyType<T>> propertyTypeClass) {
        return getProperty(entity, propertyTypeClass.getName());
    }

    @Override
    public Ack editProperty(ProjectEntity entity, String propertyTypeName, JsonNode data) {
        // Gets the property using its fully qualified type name
        PropertyType<?> propertyType = getPropertyTypeByName(propertyTypeName);
        // Edits the property
        return editProperty(entity, propertyType, data);
    }

    @Override
    public Ack deleteProperty(ProjectEntity entity, String propertyTypeName) {
        // Gets the property using its fully qualified type name
        PropertyType<?> propertyType = getPropertyTypeByName(propertyTypeName);
        // Deletes the property
        return deleteProperty(entity, propertyType);
    }

    private <T> Ack deleteProperty(ProjectEntity entity, PropertyType<T> propertyType) {
        // Checks for edition
        if (!propertyType.canEdit(entity, securityService)) {
            throw new AccessDeniedException("Property is not opened for viewing.");
        }
        // Gets the existing value
        T value = getPropertyValue(propertyType, entity);
        // If existing, deletes it
        if (value != null) {
            Ack ack = propertyRepository.deleteProperty(propertyType.getClass().getName(), entity.getProjectEntityType(), entity.getId());
            if (ack.isSuccess()) {
                // Property deletion event
                eventPostService.post(eventFactory.propertyDelete(entity, propertyType));
                // Listener
                propertyType.onPropertyDeleted(entity, value);
            }
            // OK
            return ack;
        } else {
            return Ack.NOK;
        }
    }

    @Override
    public <T> Ack editProperty(ProjectEntity entity, Class<? extends PropertyType<T>> propertyType, T data) {
        // Gets the property type by name
        PropertyType<T> actualPropertyType = getPropertyTypeByName(propertyType.getName());
        // Actual edition
        return editProperty(entity, actualPropertyType, data);
    }

    private <T> Ack editProperty(ProjectEntity entity, PropertyType<T> propertyType, JsonNode data) {
        // Gets the value and validates it
        T value = propertyType.fromClient(data);
        // Actual edition
        return editProperty(entity, propertyType, value);

    }

    private <T> Ack editProperty(ProjectEntity entity, PropertyType<T> propertyType, T value) {
        // Checks for edition
        if (!propertyType.canEdit(entity, securityService)) {
            throw new AccessDeniedException("Property is not opened for edition.");
        }
        // Gets the JSON for the storage
        JsonNode storage = propertyType.forStorage(value);
        // Stores the property
        propertyRepository.saveProperty(
                propertyType.getClass().getName(),
                entity.getProjectEntityType(),
                entity.getId(),
                storage
        );
        // Property change event
        eventPostService.post(eventFactory.propertyChange(entity, propertyType));
        // Listener
        propertyType.onPropertyChanged(entity, value);
        // OK
        return Ack.OK;
    }

    protected <T> Property<T> getProperty(PropertyType<T> type, ProjectEntity entity) {
        T value = getPropertyValue(type, entity);
        return value != null ? Property.of(type, value) : Property.empty(type);
    }

    protected <T> T getPropertyValue(PropertyType<T> type, ProjectEntity entity) {
        // Supported entity?
        if (!type.getSupportedEntityTypes().contains(entity.getProjectEntityType())) {
            throw new PropertyUnsupportedEntityTypeException(type.getClass().getName(), entity.getProjectEntityType());
        }
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

    @Override
    public <T> Collection<ProjectEntity> searchWithPropertyValue(
            Class<? extends PropertyType<T>> propertyTypeClass,
            BiFunction<ProjectEntityType, ID, ProjectEntity> entityLoader,
            Predicate<T> predicate) {
        // Gets the property type
        String propertyTypeName = propertyTypeClass.getName();
        PropertyType<T> propertyType = getPropertyTypeByName(propertyTypeName);
        // Search
        return propertyRepository.searchByProperty(
                propertyTypeName,
                entityLoader,
                t -> predicate.test(propertyType.fromStorage(t.getJson()))
        );
    }

    @Override
    @Nullable
    public <T> ID findBuildByBranchAndSearchkey(ID branchId, Class<? extends PropertyType<T>> propertyType, String searchKey) {
        // Gets the property type by name
        PropertyType<T> actualPropertyType = getPropertyTypeByName(propertyType.getName());
        // Gets the search arguments
        PropertySearchArguments searchArguments = actualPropertyType.getSearchArguments(searchKey);
        if (searchArguments != null) {
            return propertyRepository.findBuildByBranchAndSearchkey(branchId, propertyType.getName(), searchArguments);
        } else {
            return null;
        }
    }

    @Override
    public <T> List<ID> findByEntityTypeAndSearchkey(ProjectEntityType entityType, Class<? extends PropertyType<T>> propertyType, String searchKey) {
        // Gets the property type by name
        PropertyType<T> actualPropertyType = getPropertyTypeByName(propertyType.getName());
        // Gets the search arguments
        PropertySearchArguments searchArguments = actualPropertyType.getSearchArguments(searchKey);
        if (searchArguments != null) {
            return propertyRepository.findByEntityTypeAndSearchkey(entityType, propertyType.getName(), searchArguments);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public <T> void copyProperty(ProjectEntity sourceEntity, Property<T> property, ProjectEntity targetEntity, Function<String, String> replacementFn) {
        // Property copy
        T data = property.getType().copy(sourceEntity, property.getValue(), targetEntity, replacementFn);
        // Direct edition
        editProperty(targetEntity, property.getType(), data);
    }

    protected <T> Form getPropertyEditionForm(ProjectEntity entity, PropertyType<T> propertyType) {
        // Checks for edition
        if (!propertyType.canEdit(entity, securityService)) {
            throw new AccessDeniedException("Property is not opened for edition.");
        }
        // Gets the value for this property
        T value = getPropertyValue(propertyType, entity);
        // Gets the form
        return propertyType.getEditionForm(entity, value);
    }
}
