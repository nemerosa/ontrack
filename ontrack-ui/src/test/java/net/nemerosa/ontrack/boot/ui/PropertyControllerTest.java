package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.security.ProjectView;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Integration tests for the property controller.
 */
public class PropertyControllerTest extends AbstractWebTestSupport {

    @Autowired
    private PropertyController controller;

    @Autowired
    private StructureService structureService;

    /**
     * List of editable properties for a project.
     */
    @Test
    public void project_properties() throws Exception {
        Project project = asUser().with(ProjectCreation.class).call(() -> structureService.newProject(
                Project.of(nameDescription())
        ));
        Entity.isEntityDefined(project, "Project is defined");
        // Gets the editable properties for this project
        Resources<Resource<PropertyTypeDescriptor>> properties = asUser().with(project.id(), ProjectConfig.class).call(() ->
                        controller.getEditableProperties(ProjectEntityType.PROJECT, project.getId())
        );
        // Checks there is at least the Jenkins Job property
        assertNotNull("Editable properties should not be null", properties);
        assertTrue(
                "At least the Jenkins Job property should have been found",
                properties.getResources().stream()
                        .map(Resource::getData)
                        .filter(p -> "Jenkins Job".equals(p.getName()))
                        .findFirst().isPresent()
        );
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
        // Gets the editable properties for this project
        Resources<Resource<PropertyTypeDescriptor>> properties = asUser().with(project.id(), ProjectView.class).call(() ->
                        controller.getEditableProperties(ProjectEntityType.PROJECT, project.getId())
        );
        // Checks there is at least the Jenkins Job property
        assertNotNull("Editable properties should not be null", properties);
        assertFalse(
                "The Jenkins Job property should not have been found since the user is not authorized",
                properties.getResources().stream()
                        .map(Resource::getData)
                        .filter(p -> "Jenkins Job".equals(p.getName()))
                        .findFirst().isPresent()
        );
    }

}
