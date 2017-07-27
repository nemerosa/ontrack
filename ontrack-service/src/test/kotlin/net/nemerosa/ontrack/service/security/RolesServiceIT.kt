package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.*
import org.junit.Assert.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.IllegalStateException

open class RolesServiceIT : AbstractServiceTestSupport() {

    @Autowired
    var rolesService: RolesService? = null

    @Autowired
    var securityService: SecurityService? = null

    interface TestGlobalFunction : GlobalFunction
    interface TestProject1Function : ProjectFunction
    interface TestProject2Function : ProjectFunction
    @CoreFunction
    interface TestProjectCoreFunction : ProjectFunction

    @Configuration
    open class RoleTestContributors {
        @Bean
        open fun roleContributor(): RoleContributor {
            return object : RoleContributor {
                override fun getGlobalFunctionContributionsForGlobalRole(role: String): List<Class<out GlobalFunction>> =
                        when (role) {
                            Roles.GLOBAL_CONTROLLER -> listOf(TestGlobalFunction::class.java)
                            else -> listOf()
                        }

                override fun getProjectFunctionContributionsForGlobalRole(role: String): List<Class<out ProjectFunction>> =
                        when (role) {
                            Roles.GLOBAL_CREATOR -> listOf(TestProject1Function::class.java)
                            else -> listOf()
                        }

                override fun getProjectFunctionContributionsForProjectRole(role: String): List<Class<out ProjectFunction>> =
                        when (role) {
                            Roles.PROJECT_OWNER -> listOf(TestProject2Function::class.java)
                            else -> listOf()
                        }
            }
        }
    }

    @Test
    fun roles_contributions() {
        val globalController = rolesService!!.getGlobalRole(Roles.GLOBAL_CONTROLLER).orElse(null)
        assertNotNull(globalController)
        assertTrue(TestGlobalFunction::class.java in globalController.globalFunctions)

        val globalCreator = rolesService!!.getGlobalRole(Roles.GLOBAL_CREATOR).orElse(null)
        assertNotNull(globalCreator)
        assertTrue(TestProject1Function::class.java in globalCreator.projectFunctions)

        val projectOwner = rolesService!!.getProjectRole(Roles.PROJECT_OWNER).orElse(null)
        assertNotNull(projectOwner)
        assertTrue(TestProject2Function::class.java in projectOwner.functions)
    }

    @Test
    fun only_non_core_functions_are_allowed() {
        val service = RolesServiceImpl(listOf(object : RoleContributor {
            override fun getProjectFunctionContributionsForProjectRole(role: String): List<Class<out ProjectFunction>> =
                    when (role) {
                        Roles.PROJECT_OWNER -> listOf(TestProjectCoreFunction::class.java)
                        else -> listOf()
                    }
        }))
        try {
            service.start()
            fail("It should not have been possible to add a core function to a role.")
        } catch (e: IllegalStateException) {
            assertEquals("A core function cannot be added to an existing role.", e.message)
        }
    }

    @Test
    fun trying_to_add_an_existing_core_function() {
        val service = RolesServiceImpl(listOf(object : RoleContributor {
            override fun getProjectFunctionContributionsForProjectRole(role: String): List<Class<out ProjectFunction>> =
                    when (role) {
                        Roles.PROJECT_PARTICIPANT -> listOf(ProjectConfig::class.java)
                        else -> listOf()
                    }
        }))
        try {
            service.start()
            fail("It should not have been possible to add an existing core function to a role.")
        } catch (e: IllegalStateException) {
            assertEquals("A core function cannot be added to an existing role.", e.message)
        }
    }

    @Test
    fun testing_a_global_role() {
        val project = doCreateProject()
        asAccount(doCreateAccountWithGlobalRole(Roles.GLOBAL_CONTROLLER)).call {
            assertTrue(securityService!!.isGlobalFunctionGranted(TestGlobalFunction::class.java))
            assertFalse(securityService!!.isProjectFunctionGranted(project, TestProject1Function::class.java))
            assertFalse(securityService!!.isProjectFunctionGranted(project, TestProject2Function::class.java))
        }
    }

    @Test
    fun testing_a_global_role_with_project_function() {
        val project = doCreateProject()
        asAccount(doCreateAccountWithGlobalRole(Roles.GLOBAL_CREATOR)).call {
            assertFalse(securityService!!.isGlobalFunctionGranted(TestGlobalFunction::class.java))
            assertTrue(securityService!!.isProjectFunctionGranted(project, TestProject1Function::class.java))
            assertFalse(securityService!!.isProjectFunctionGranted(project, TestProject2Function::class.java))
        }
    }

    @Test
    fun testing_a_project_role() {
        val project = doCreateProject()
        asAccount(doCreateAccountWithProjectRole(project, Roles.PROJECT_OWNER)).call {
            assertFalse(securityService!!.isGlobalFunctionGranted(TestGlobalFunction::class.java))
            assertFalse(securityService!!.isProjectFunctionGranted(project, TestProject1Function::class.java))
            assertTrue(securityService!!.isProjectFunctionGranted(project, TestProject2Function::class.java))
        }
    }

    @Test
    fun testing_a_neutral_project_role() {
        val project = doCreateProject()
        asAccount(doCreateAccountWithProjectRole(project, Roles.PROJECT_PARTICIPANT)).call {
            assertFalse(securityService!!.isGlobalFunctionGranted(TestGlobalFunction::class.java))
            assertFalse(securityService!!.isProjectFunctionGranted(project, TestProject1Function::class.java))
            assertFalse(securityService!!.isProjectFunctionGranted(project, TestProject2Function::class.java))
        }
    }

}