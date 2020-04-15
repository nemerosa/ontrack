package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Project
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Testing the basic security conditions.
 */
class SecuritySetupIT : AbstractDSLTestSupport() {

    @Test
    fun `Authentication is required to create a project`() {
        assertFailsWith<AccessDeniedException> {
            asAnonymous {
                structureService.newProject(Project.of(nameDescription()))
            }
        }
    }

    @Test
    fun `Admin user can create a project`() {
        val project = asAdmin {
            structureService.newProject(Project.of(nameDescription()))
        }
        assertTrue(project.id.isSet, "Project has been created")
    }

}