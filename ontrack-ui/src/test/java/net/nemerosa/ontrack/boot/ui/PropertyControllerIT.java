package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty;
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType;
import net.nemerosa.ontrack.model.form.Field;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.security.ProjectView;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static org.junit.Assert.*;

/**
 * Integration tests for the property controller.
 */
public class PropertyControllerIT extends AbstractWebTestSupport {

    @Autowired
    private PropertyController controller;

    @Autowired
    private StructureService structureService;

    /**
     * List of editable properties for a project.
     */
    @Test
    public void project_properties_with_edition_allowed() throws Exception {
        Project project = asUser().with(ProjectCreation.class).call(() -> structureService.newProject(
                Project.of(nameDescription())
        ));
        Entity.isEntityDefined(project, "Project is defined");
        // Gets the properties for this project
        Resources<Resource<Property<?>>> properties = asUser().with(project.id(), ProjectEdit.class).call(() ->
                controller.getProperties(ProjectEntityType.PROJECT, project.getId())
        );
        // Checks there is at least the Jenkins Job property
        assertNotNull("Properties should not be null", properties);
        Optional<Property<?>> property = properties.getResources().stream()
                .map(Resource::getData)
                .filter(p -> "Simple value".equals(p.getType().getName()))
                .findFirst();
        assertTrue("At least the Simple value property should have been found", property.isPresent());
        assertTrue("The Simple value property should be editable", property.get().isEditable());
    }

    /**
     * List of editable properties for a project, filter by authorization.
     */
    @Test
    public void project_properties_filtered_by_authorization() throws Exception {
        Project project = asUser().with(ProjectCreation.class).call(() -> structureService.newProject(
                Project.of(nameDescription())
        ));
        Entity.isEntityDefined(project, "Project is defined");
        // Gets the properties for this project
        Resources<Resource<Property<?>>> properties = asUser().with(project.id(), ProjectView.class).call(() ->
                controller.getProperties(ProjectEntityType.PROJECT, project.getId())
        );
        // Checks there is at least the Jenkins Job property
        assertNotNull("Properties should not be null", properties);
        Optional<Property<?>> property = properties.getResources().stream()
                .map(Resource::getData)
                .filter(p -> "Simple value".equals(p.getType().getName()))
                .findFirst();
        assertTrue("At least the Simple value property should have been found", property.isPresent());
        assertFalse("The Simple value property should not be editable", property.get().isEditable());
    }

    /**
     * Edition form for an existing property.
     */
    @Test
    public void property_edition_form_for_an_existing_property() throws Exception {
        // Creates a project
        Project project = asUser().with(ProjectCreation.class).call(() -> structureService.newProject(
                Project.of(nameDescription())
        ));
        asUser()
                .with(project.id(), ProjectEdit.class)
                .call(
                        () -> {
                            controller.editProperty(ProjectEntityType.PROJECT, project.getId(), TestSimplePropertyType.class.getName(),
                                    object()
                                            .with("value", "Message")
                                            .end()
                            );
                            // Gets the property
                            Resource<Property<?>> propertyResource = controller.getPropertyValue(ProjectEntityType.PROJECT, project.getId(), TestSimplePropertyType.class.getName());
                            assertNotNull(propertyResource);
                            @SuppressWarnings("unchecked")
                            Property<TestSimpleProperty> property = (Property<TestSimpleProperty>) propertyResource.getData();
                            // Checks the property
                            assertFalse(property.isEmpty());
                            TestSimpleProperty value = property.getValue();
                            assertNotNull(value);
                            // Checks the property content
                            assertEquals("Message", value.getValue());
                            // Gets the edition form
                            Form form = controller.getPropertyEditionForm(ProjectEntityType.PROJECT, project.getId(), TestSimplePropertyType.class.getName());
                            assertEquals(1, form.getFields().size());
                            {
                                Field f = form.getField("value");
                                assertNotNull(f);
                                assertEquals("Message", f.getValue());
                            }
                            // End
                            return null;
                        }
                );
    }

}
