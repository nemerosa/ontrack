package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * UI end point for the management of properties.
 */
@RestController
@RequestMapping("/properties")
public class PropertyController extends AbstractResourceController {

    private final PropertyService propertyService;
    private final StructureService structureService;

    @Autowired
    public PropertyController(PropertyService propertyService, StructureService structureService) {
        this.propertyService = propertyService;
        this.structureService = structureService;
    }

    /**
     * Gets the list of properties for a given entity and for the current user.
     *
     * @param entityType Entity type
     * @param id         Entity ID
     * @return List of properties
     */
    @RequestMapping(value = "{entityType}/{id}/view", method = RequestMethod.GET)
    public Resources<Property<?>> getProperties(@PathVariable ProjectEntityType entityType, @PathVariable ID id) {
        ProjectEntity entity = getEntity(entityType, id);
        return Resources.of(
                propertyService.getProperties(entity),
                uri(on(getClass()).getProperties(entityType, id))
        );
    }

    /**
     * Gets the list of editable properties for a given entity and for the current user.
     *
     * @param entityType Entity type
     * @param id         Entity ID
     * @return List of editable properties
     */
    @RequestMapping(value = "{entityType}/{id}/editable", method = RequestMethod.GET)
    public Resources<PropertyTypeDescriptor> getEditableProperties(@PathVariable ProjectEntityType entityType, @PathVariable ID id) {
        ProjectEntity entity = getEntity(entityType, id);
        return Resources.of(
                propertyService.getEditableProperties(entity),
                uri(on(getClass()).getEditableProperties(entityType, id))
        );
    }

    protected ProjectEntity getEntity(ProjectEntityType entityType, ID id) {
        return entityType.getEntityFn(structureService).apply(id);
    }
}
