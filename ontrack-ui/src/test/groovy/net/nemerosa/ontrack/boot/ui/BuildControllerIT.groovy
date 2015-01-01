package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.extension.jenkins.JenkinsBuildProperty
import net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.JenkinsController
import net.nemerosa.ontrack.model.security.BranchCreate
import net.nemerosa.ontrack.model.security.BuildCreate
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectCreation
import net.nemerosa.ontrack.model.structure.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static net.nemerosa.ontrack.json.JsonUtils.object
import static net.nemerosa.ontrack.test.TestUtils.uid
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class BuildControllerIT extends AbstractWebTestSupport {

    @Autowired
    BuildController buildController;

    @Autowired
    StructureService structureService;

    @Autowired
    JenkinsController jenkinsController;

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
        // Creates a Jenkins configuration
        String configurationName = uid("C");
        asUser().with(GlobalSettings).call {
            jenkinsController.newConfiguration(
                    new JenkinsConfiguration(
                            configurationName,
                            "http://jenkins",
                            "",
                            ""
                    )
            )
        }
        // Creates a build with a Jenkins build property
        Build build = asUser().with(project, BuildCreate).call {
            buildController.newBuild(
                    branch.getId(),
                    new BuildRequest(
                            "12",
                            "Build 12",
                            Collections.singletonList(
                                    new PropertyCreationRequest(
                                            JenkinsBuildPropertyType.class.getName(),
                                            object()
                                                    .with("configuration", configurationName)
                                                    .with("job", "MyJob")
                                                    .with("build", 12)
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
        Property<JenkinsBuildProperty> property = propertyService.getProperty(build, JenkinsBuildPropertyType.class.getName());
        assertNotNull(property);
        assertEquals(configurationName, property.getValue().getConfiguration().getName());
        assertEquals("MyJob", property.getValue().getJob());
        assertEquals(12, property.getValue().getBuild());
        assertEquals("http://jenkins/job/MyJob/12", property.getValue().getUrl());
    }
}