package net.nemerosa.ontrack.service.support.configuration;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.model.support.AbstractConfigurationPropertyCleanupListener;
import net.nemerosa.ontrack.service.support.property.TestProperty;
import net.nemerosa.ontrack.service.support.property.TestPropertyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestConfigurationPropertyCleanupListener extends AbstractConfigurationPropertyCleanupListener<TestConfiguration, TestProperty> {
    @Autowired
    public TestConfigurationPropertyCleanupListener(PropertyService propertyService, StructureService structureService, SecurityService securityService) {
        super(propertyService, structureService, securityService, TestPropertyType.class);
    }
}
