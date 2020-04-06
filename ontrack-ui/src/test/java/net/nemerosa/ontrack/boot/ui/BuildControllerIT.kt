package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.structure.BuildRequest
import net.nemerosa.ontrack.model.structure.Entity
import net.nemerosa.ontrack.model.structure.PropertyCreationRequest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BuildControllerIT : AbstractWebTestSupport() {

    @Autowired
    private lateinit var buildController: BuildController

    @Test
    fun `New build with properties`() {
        // Creates a project
        project {
            // Creates a branch
            branch {
                // Creates a build with a release property using the controller
                asUser().with(this, ProjectEdit::class.java).call {
                    val build = buildController.newBuild(
                            id,
                            BuildRequest(
                                    "12",
                                    "Build 12",
                                    listOf(
                                            PropertyCreationRequest(
                                                    TestSimplePropertyType::class.java.name,
                                                    TestSimpleProperty("RC").asJson()
                                            )
                                    )
                            )
                    )
                    // Checks the build
                    Entity.isEntityDefined(build, "Build is defined")
                    assertEquals(this, build.branch)
                    assertEquals("12", build.name)
                    assertEquals("Build 12", build.description)
                    // Checks the Jenkins build property
                    val property = build.property(TestSimplePropertyType::class)
                    assertNotNull(property) {
                        assertEquals("RC", it.value)
                    }
                }
            }
        }
    }
}