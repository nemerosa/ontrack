package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.ProjectCreation
import org.apache.commons.lang3.StringUtils
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.test.*

class SecurityServiceIT : AbstractServiceTestSupport() {

    @Test
    fun currentAccount() {
        val account = asUser().call { securityService.currentAccount }
        assertNotNull(account)
    }

    @Test
    fun currentAccount_none() {
        val account = asAnonymous().call { securityService.currentAccount }
        assertNull(account)
    }

    @Test
    fun runner_function() {
        // Function that needs an authentication context
        val fn = { s: String -> "$s -> $contextName" }
        // Testing outside a context
        assertEquals("test -> none", fn("test"))
        // With a context
        val securedFn = asUser().with(ProjectCreation::class.java).call { securityService.runner(fn) }
        // Calls the secured function
        assertEquals("test -> TestingAuthenticationToken", securedFn("test"))
    }

    @Test
    fun read_only_on_one_project() {
        withNoGrantViewToAll {

            // Creates two projects
            val (id, name) = doCreateProject()
            val p2 = doCreateProject()
            // Creates an account authorised to access only one project
            val account = doCreateAccountWithProjectRole(p2, "READ_ONLY")
            asAccount(account).call {

                // With this account, gets the list of projects
                val list = structureService.projectList
                // Checks we only have one project
                assertEquals(1, list.size.toLong())
                assertEquals(p2.name, list[0].name)
                // Access to the authorised project
                assertTrue(structureService.findProjectByName(p2.name).isPresent)
                assertNotNull(structureService.getProject(p2.id))
                // No access to the other project
                assertFalse(structureService.findProjectByName(name).isPresent)
                try {
                    structureService.getProject(id)
                    fail("Project is not authorised")
                } catch (ignored: AccessDeniedException) {
                    assertTrue(true, "Project cannot be found")
                }
                true
            }
        }
    }

    @Test
    fun read_only_on_all_projects() {
        withNoGrantViewToAll {

            // Creates two projects
            val (id, name) = doCreateProject()
            val (id1, name1) = doCreateProject()
            // Creates an account authorised to access all projects
            val account = doCreateAccountWithGlobalRole("READ_ONLY")
            asAccount(account).call {

                // With this account, gets the list of projects
                val list = structureService.projectList
                // Checks we only have the two projects (among all others)
                assertTrue(list.size >= 2)
                assertTrue(list.stream().anyMatch { (_, name2) -> StringUtils.equals(name, name2) })
                assertTrue(list.stream().anyMatch { (_, name2) -> StringUtils.equals(name1, name2) })
                // Access to the projects
                assertTrue(structureService.findProjectByName(name).isPresent)
                assertNotNull(structureService.getProject(id))
                assertTrue(structureService.findProjectByName(name1).isPresent)
                assertNotNull(structureService.getProject(id1))
                true
            }
        }
    }

    companion object {
        private val contextName: String
            get() {
                val authentication = SecurityContextHolder.getContext().authentication
                return if (authentication != null) authentication.javaClass.simpleName else "none"
            }
    }
}