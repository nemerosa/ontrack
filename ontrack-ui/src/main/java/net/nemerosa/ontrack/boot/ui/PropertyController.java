package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

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
                // TODO Obfuscation of sensitive data
                propertyService.getProperties(entity),
                uri(on(getClass()).getProperties(entityType, id))
        );
    }

    /**
     * Gets the list of editable properties for a given entity and for the current user.
     * <p/>
     * Each property entry is associated with its link to the form.
     *
     * @param entityType Entity type
     * @param id         Entity ID
     * @return List of editable properties
     */
    @RequestMapping(value = "{entityType}/{id}/editable", method = RequestMethod.GET)
    public Resources<Resource<PropertyTypeDescriptor>> getEditableProperties(@PathVariable ProjectEntityType entityType, @PathVariable ID id) {
        ProjectEntity entity = getEntity(entityType, id);
        return Resources.of(
                propertyService.getEditableProperties(entity).stream()
                        .map(p -> toEditablePropertyResource(p, entity))
                        .collect(Collectors.toList()),
                uri(on(getClass()).getEditableProperties(entityType, id))
        );
    }

    /**
     * Gets the edition form for a given property for an entity. The content of the form may be filled or not,
     * according to the fact if the property is actually set for this entity or not. If the property is not
     * opened for edition, the call could be rejected with an authorization exception.
     *
     * @param entityType       Type of the entity to get the edition form for
     * @param id               ID of the entity to get the edition form for
     * @param propertyTypeName Fully qualified name of the property to get the form for
     * @return An edition form to be used by the client
     */
    @RequestMapping(value = "{entityType}/{id}/{propertyTypeName}/edit", method = RequestMethod.GET)
    public Form getPropertyEditionForm(@PathVariable ProjectEntityType entityType, @PathVariable ID id, @PathVariable String propertyTypeName) {
        return propertyService.getPropertyEditionForm(
                getEntity(entityType, id),
                propertyTypeName
        );
    }

    protected Resource<PropertyTypeDescriptor> toEditablePropertyResource(PropertyTypeDescriptor propertyTypeDescriptor, ProjectEntity entity) {
        return Resource.of(
                propertyTypeDescriptor,
                uri(on(getClass()).getPropertyEditionForm(entity.getProjectEntityType(), entity.getId(), propertyTypeDescriptor.getTypeName()))
        );
    }

    protected ProjectEntity getEntity(ProjectEntityType entityType, ID id) {
        return entityType.getEntityFn(structureService).apply(id);
    }
}
