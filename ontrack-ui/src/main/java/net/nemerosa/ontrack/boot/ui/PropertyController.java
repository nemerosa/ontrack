package net.nemerosa.ontrack.boot.ui;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * UI end point for the management of properties.
 */
@RestController
@RequestMapping("/rest/properties")
public class PropertyController extends AbstractProjectEntityController {

    private final PropertyService propertyService;

    @Autowired
    public PropertyController(PropertyService propertyService, StructureService structureService) {
        super(structureService);
        this.propertyService = propertyService;
    }

    /**
     * Gets the list of properties for a given entity and for the current user.
     *
     * @param entityType Entity type
     * @param id         Entity ID
     * @return List of properties
     */
    @RequestMapping(value = "{entityType}/{id}", method = RequestMethod.GET)
    public Resources<Resource<Property<?>>> getProperties(@PathVariable ProjectEntityType entityType, @PathVariable ID id) {
        ProjectEntity entity = getEntity(entityType, id);
        List<Property<?>> properties = propertyService.getProperties(entity);
        List<Resource<Property<?>>> resources = new ArrayList<>();
        for (Property<?> property : properties) {
            Resource<Property<?>> resource = Resource.of(
                    property,
                    uri(on(getClass()).getPropertyValue(entity.getProjectEntityType(), entity.getId(), property.getType().getClass().getName()))
            );
            // Update
            resource = resource.with(
                    Link.UPDATE,
                    uri(on(getClass()).getPropertyEditionForm(entity.getProjectEntityType(), entity.getId(), property.getType().getClass().getName())));
            // OK
            resources.add(resource);
        }
        return Resources.of(
                resources,
                uri(on(getClass()).getProperties(entityType, id))
        );
    }

    /**
     * Gets the value for a given property for an entity. If the property is not set, a non-null
     * {@link net.nemerosa.ontrack.model.structure.Property} is returned but is marked as
     * {@linkplain net.nemerosa.ontrack.model.structure.Property#isEmpty() empty}.
     * If the property is not opened for viewing, the call could be rejected with an
     * authorization exception.
     *
     * @param entityType       Type of the entity to get the edition form for
     * @param id               ID of the entity to get the edition form for
     * @param propertyTypeName Fully qualified name of the property to get the form for
     * @return A response that defines the property
     */
    @RequestMapping(value = "{entityType}/{id}/{propertyTypeName}/view", method = RequestMethod.GET)
    public Resource<Property<?>> getPropertyValue(@PathVariable ProjectEntityType entityType, @PathVariable ID id, @PathVariable String propertyTypeName) {
        return Resource.of(
                propertyService.getProperty(getEntity(entityType, id), propertyTypeName),
                uri(on(getClass()).getPropertyValue(entityType, id, propertyTypeName))
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

    /**
     * Edits the value of a property.
     *
     * @param entityType       Type of the entity to edit
     * @param id               ID of the entity to edit
     * @param propertyTypeName Fully qualified name of the property to edit
     * @param data             Raw JSON data for the property value
     */
    @RequestMapping(value = "{entityType}/{id}/{propertyTypeName}/edit", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Ack editProperty(@PathVariable ProjectEntityType entityType, @PathVariable ID id, @PathVariable String propertyTypeName, @RequestBody JsonNode data) {
        return propertyService.editProperty(
                getEntity(entityType, id),
                propertyTypeName,
                data
        );
    }

    /**
     * Deletes the value of a property.
     *
     * @param entityType       Type of the entity to edit
     * @param id               ID of the entity to edit
     * @param propertyTypeName Fully qualified name of the property to delete
     */
    @RequestMapping(value = "{entityType}/{id}/{propertyTypeName}/edit", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Ack deleteProperty(@PathVariable ProjectEntityType entityType, @PathVariable ID id, @PathVariable String propertyTypeName) {
        return propertyService.deleteProperty(
                getEntity(entityType, id),
                propertyTypeName
        );
    }

}
