package net.nemerosa.ontrack.model.support;

import net.nemerosa.ontrack.model.exceptions.PropertyTypeNotFoundException;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.Property;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.apache.commons.lang3.StringUtils;

public class PropertyServiceHelper {

    public static boolean hasProperty(PropertyService propertyService,
                                      ProjectEntity entity,
                                      String propertyTypeName,
                                      String propertyValue) {
        try {
            Property<?> property = propertyService.getProperty(entity, propertyTypeName);
            return !property.isEmpty()
                    && (
                    StringUtils.isBlank(propertyValue)
                            || property.containsValue(propertyValue));
        } catch (PropertyTypeNotFoundException ex) {
            return false;
        }
    }

}
