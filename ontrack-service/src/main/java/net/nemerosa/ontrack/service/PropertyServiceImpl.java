package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.PropertyTypeExtension;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.PropertyRepository;
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
        // FIXME Method net.nemerosa.ontrack.service.PropertyServiceImpl.getProperties
        return null;
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
