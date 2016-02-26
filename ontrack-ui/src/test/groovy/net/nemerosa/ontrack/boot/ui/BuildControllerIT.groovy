package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.model.security.BranchCreate
import net.nemerosa.ontrack.model.security.ProjectCreation
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.json.JsonUtils.object
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class BuildControllerIT extends AbstractWebTestSupport {

    @Autowired
    BuildController buildController;

    @Autowired
    StructureService structureService;

    @Autowired
    PropertyService propertyService;

    @Test
    void 'New build with properties'() {
        // Creates a project
        Project project = asUser().with(ProjectCreation).call {
            structureService.newProject(
                    Project.of(nameDescription())
            )
        }
        // Creates a branch
        Branch branch = asUser().with(project, BranchCreate).call {
            structureService.newBranch(
                    Branch.of(project, nameDescription())
            )
        }
        // Creates a build with a release property
        Build build = asUser().with(project, ProjectEdit).call {
            buildController.newBuild(
                    branch.getId(),
                    new BuildRequest(
                            "12",
                            "Build 12",
                            Collections.singletonList(
                                    new PropertyCreationRequest(
                                            TestSimplePropertyType.class.getName(),
                                            object()
                                                    .with("value", "RC")
                                                    .end()
                                    )
                            )
                    )
            )
        }
        // Checks the build
        Entity.isEntityDefined(build, "Build is defined");
        assertEquals(branch, build.getBranch());
        assertEquals("12", build.getName());
        assertEquals("Build 12", build.getDescription());
        // Checks the Jenkins build property
        Property<TestSimpleProperty> property = propertyService.getProperty(build, TestSimplePropertyType.class.getName());
        assertNotNull(property);
        assertEquals("RC", property.getValue().getValue());
    }
}