package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.Property;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.PropertyType;
import net.nemerosa.ontrack.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final SecurityService securityService;

    @Autowired
    public PropertyServiceImpl(PropertyRepository propertyRepository, SecurityService securityService) {
        this.propertyRepository = propertyRepository;
        this.securityService = securityService;
    }

    @Override
    public List<PropertyType<?>> getPropertyTypes() {
        // FIXME Method net.nemerosa.ontrack.service.PropertyServiceImpl.getPropertyTypes
        return null;
    }

    @Override
    public List<Property<?>> getProperties(Entity entity) {
        // FIXME Method net.nemerosa.ontrack.service.PropertyServiceImpl.getProperties
        return null;
    }
}
