package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.ProjectCreation
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

    @Test
    fun `Being authenticated is not enough to create a project`() {
        assertFailsWith<AccessDeniedException> {
            asUser().call {
                structureService.newProject(Project.of(nameDescription()))
            }
        }
    }

    @Test
    fun `Granting just enough rights`() {
        asUserWith<ProjectCreation> {
            structureService.newProject(Project.of(nameDescription()))
        }
    }

    @Test
    fun `Testing DSL to create a project`() {
        project {
            assertTrue(id.isSet, "Project has been created")
        }
    }

    @Test
    fun `Viewing projects is granted by default to all authenticated users`() {
        val project = project {
            branch()
        }
        val branches = asUser().call {
            structureService.getBranchesForProject(project.id)
        }
        assertTrue(branches.isNotEmpty(), "Could access the branches")
    }

    @Test
    fun `Viewing projects can be disabled for all authenticated users`() {
        val project = project {
            branch()
        }
        withNoGrantViewToAll {
            asUser().call {
                assertFailsWith<AccessDeniedException> {
                    structureService.getBranchesForProject(project.id)
                }
            }
        }
    }

    @Test
    fun `Viewing projects needs to be granted explicitly when disabled globally`() {
        val project = project {
            branch()
        }
        withNoGrantViewToAll {
            val branches = asUser().withView(project).call {
                structureService.getBranchesForProject(project.id)
            }
            assertTrue(branches.isNotEmpty(), "Could access the branches")
        }
    }

}