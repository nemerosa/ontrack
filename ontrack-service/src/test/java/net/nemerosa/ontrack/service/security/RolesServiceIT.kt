package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.*
import org.junit.Assert.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.IllegalStateException

const val newGlobalRole = "NEW_GLOBAL"
const val extGlobalRole = "EXT_AUTOMATER"
const val newProjectRole = "NEW_PROJECT"
const val extProjectRole = "EXT_PARTICIPANT"

class RolesServiceIT : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var rolesService: RolesService

    @Autowired
    private lateinit var securityService: SecurityService

    interface TestGlobalFunction : GlobalFunction
    interface TestProject1Function : ProjectFunction
    interface TestProject2Function : ProjectFunction
    @CoreFunction
    interface TestProjectCoreFunction : ProjectFunction

    @Configuration
    class RoleTestContributors {
        @Bean
        fun roleContributor(): RoleContributor {
            return object : RoleContributor {
                override fun getGlobalRoles(): List<RoleDefinition> = listOf(
                        RoleDefinition(newGlobalRole, "New global role", "Test for a new global role"),
                        RoleDefinition(
                                extGlobalRole,
                                "Extended automater",
                                "Automater with extra functions",
                                Roles.GLOBAL_AUTOMATION
                        )
                )

                override fun getProjectRoles(): List<RoleDefinition> = listOf(
                        RoleDefinition(newProjectRole, "New project", "Test for a new project role"),
                        RoleDefinition(
                                extProjectRole,
                                "Extended participant",
                                "Participant with extra functions",
                                Roles.PROJECT_PARTICIPANT
                        )
                )

                override fun getGlobalFunctionContributionsForGlobalRoles(): Map<String, List<Class<out GlobalFunction>>> =
                        mapOf(
                                Roles.GLOBAL_CONTROLLER to listOf(TestGlobalFunction::class.java),
                                newGlobalRole to listOf(ProjectCreation::class.java, TestGlobalFunction::class.java),
                                extGlobalRole to listOf(TestGlobalFunction::class.java)
                        )

                override fun getProjectFunctionContributionsForGlobalRoles(): Map<String, List<Class<out ProjectFunction>>> =
                        mapOf(
                                Roles.GLOBAL_CREATOR to listOf(TestProject1Function::class.java),
                                newGlobalRole to listOf(TestProject2Function::class.java),
                                extGlobalRole to listOf(TestProject1Function::class.java)
                        )

                override fun getProjectFunctionContributionsForProjectRoles(): Map<String, List<Class<out ProjectFunction>>> =
                        mapOf(
                                Roles.PROJECT_OWNER to listOf(TestProject2Function::class.java),
                                newProjectRole to listOf(TestProject2Function::class.java),
                                extProjectRole to listOf(TestProject1Function::class.java)
                        )
            }
        }
    }

    @Test
    fun roles_contributions() {
        val globalController = rolesService.getGlobalRole(Roles.GLOBAL_CONTROLLER).orElse(null)
        assertNotNull(globalController)
        assertTrue(TestGlobalFunction::class.java in globalController.globalFunctions)

        val globalCreator = rolesService.getGlobalRole(Roles.GLOBAL_CREATOR).orElse(null)
        assertNotNull(globalCreator)
        assertTrue(TestProject1Function::class.java in globalCreator.projectFunctions)

        val projectOwner = rolesService.getProjectRole(Roles.PROJECT_OWNER).orElse(null)
        assertNotNull(projectOwner)
        assertTrue(TestProject2Function::class.java in projectOwner.functions)
    }

    @Test
    fun only_non_core_functions_are_allowed() {
        val service = RolesServiceImpl(listOf(object : RoleContributor {
            override fun getProjectFunctionContributionsForProjectRoles(): Map<String, List<Class<out ProjectFunction>>> =
                    mapOf(
                            Roles.PROJECT_OWNER to listOf(TestProjectCoreFunction::class.java)
                    )
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
            override fun getProjectFunctionContributionsForProjectRoles(): Map<String, List<Class<out ProjectFunction>>> =
                    mapOf(
                            Roles.PROJECT_PARTICIPANT to listOf(ProjectConfig::class.java)
                    )
        }))
        try {
            service.start()
            fail("It should not have been possible to add an existing core function to a role.")
        } catch (e: IllegalStateException) {
            assertEquals("A core function cannot be added to an existing role.", e.message)
        }
    }

    @Test
    fun trying_to_override_a_global_role() {
        val service = RolesServiceImpl(listOf(object : RoleContributor {
            override fun getGlobalRoles(): List<RoleDefinition> =
                    listOf(RoleDefinition(Roles.GLOBAL_CREATOR, "Creator", "Overridden creator role"))
        }))
        try {
            service.start()
            fail("It should not have been possible to override an existing global role.")
        } catch (e: IllegalStateException) {
            assertEquals("An existing global role cannot be overridden: " + Roles.GLOBAL_CREATOR, e.message)
        }
    }

    @Test
    fun trying_to_override_a_project_role() {
        val service = RolesServiceImpl(listOf(object : RoleContributor {
            override fun getProjectRoles(): List<RoleDefinition> =
                    listOf(RoleDefinition(Roles.PROJECT_OWNER, "Owner", "Overridden owner role"))
        }))
        try {
            service.start()
            fail("It should not have been possible to override an existing project role.")
        } catch (e: IllegalStateException) {
            assertEquals("An existing project role cannot be overridden: " + Roles.PROJECT_OWNER, e.message)
        }
    }

    @Test
    fun testing_a_global_role() {
        val project = doCreateProject()
        asAccount(doCreateAccountWithGlobalRole(Roles.GLOBAL_CONTROLLER)).call {
            assertTrue(securityService.isGlobalFunctionGranted(TestGlobalFunction::class.java))
            assertFalse(securityService.isProjectFunctionGranted(project, TestProject1Function::class.java))
            assertFalse(securityService.isProjectFunctionGranted(project, TestProject2Function::class.java))
        }
    }

    @Test
    fun testing_a_contributed_global_role() {
        val project = doCreateProject()
        asAccount(doCreateAccountWithGlobalRole(newGlobalRole)).call {
            assertTrue(securityService.isGlobalFunctionGranted(TestGlobalFunction::class.java))
            assertTrue(securityService.isGlobalFunctionGranted(ProjectCreation::class.java))
            assertFalse(securityService.isProjectFunctionGranted(project, TestProject1Function::class.java))
            assertTrue(securityService.isProjectFunctionGranted(project, TestProject2Function::class.java))
        }
    }

    @Test
    fun testing_a_global_role_with_project_function() {
        val project = doCreateProject()
        asAccount(doCreateAccountWithGlobalRole(Roles.GLOBAL_CREATOR)).call {
            assertFalse(securityService.isGlobalFunctionGranted(TestGlobalFunction::class.java))
            assertTrue(securityService.isProjectFunctionGranted(project, TestProject1Function::class.java))
            assertFalse(securityService.isProjectFunctionGranted(project, TestProject2Function::class.java))
        }
    }

    @Test
    fun testing_a_project_role() {
        val project = doCreateProject()
        asAccount(doCreateAccountWithProjectRole(project, Roles.PROJECT_OWNER)).call {
            assertFalse(securityService.isGlobalFunctionGranted(TestGlobalFunction::class.java))
            assertFalse(securityService.isProjectFunctionGranted(project, TestProject1Function::class.java))
            assertTrue(securityService.isProjectFunctionGranted(project, TestProject2Function::class.java))
        }
    }

    @Test
    fun testing_a_contributed_project_role() {
        val project = doCreateProject()
        asAccount(doCreateAccountWithProjectRole(project, newProjectRole)).call {
            assertFalse(securityService.isGlobalFunctionGranted(TestGlobalFunction::class.java))
            assertFalse(securityService.isProjectFunctionGranted(project, TestProject1Function::class.java))
            assertTrue(securityService.isProjectFunctionGranted(project, TestProject2Function::class.java))
        }
    }

    @Test
    fun testing_a_neutral_project_role() {
        val project = doCreateProject()
        asAccount(doCreateAccountWithProjectRole(project, Roles.PROJECT_PARTICIPANT)).call {
            assertFalse(securityService.isGlobalFunctionGranted(TestGlobalFunction::class.java))
            assertFalse(securityService.isProjectFunctionGranted(project, TestProject1Function::class.java))
            assertFalse(securityService.isProjectFunctionGranted(project, TestProject2Function::class.java))
        }
    }

    @Test
    fun `Global role contribution`() {
        assertNotNull(rolesService.globalRoles.find { it.id == newGlobalRole })
    }

    @Test
    fun `Project role contribution`() {
        assertNotNull(rolesService.projectRoles.find { it.id == newProjectRole })
    }

    @Test
    fun `Contributed functions must be assigned by default to the administrator role`() {
        val project = doCreateProject()
        asAccount(doCreateAccountWithGlobalRole(Roles.GLOBAL_ADMINISTRATOR)).call {
            assertTrue(securityService.isGlobalFunctionGranted(TestGlobalFunction::class.java))
            assertTrue(securityService.isProjectFunctionGranted(project, TestProject1Function::class.java))
            assertTrue(securityService.isProjectFunctionGranted(project, TestProject2Function::class.java))
        }
    }

    @Test
    fun `Global role functions are inherited`() {
        val project = doCreateProject()
        asAccount(doCreateAccountWithGlobalRole(extGlobalRole)).call {
            // Specific functions
            assertTrue(securityService.isGlobalFunctionGranted(TestGlobalFunction::class.java))
            assertTrue(securityService.isProjectFunctionGranted(project, TestProject1Function::class.java))
            assertFalse(securityService.isProjectFunctionGranted(project, TestProject2Function::class.java))
            // Inherited functions
            listOf(
                    ProjectCreation::class.java,
                    AccountGroupManagement::class.java
            ).forEach { fn ->
                assertTrue(securityService.isGlobalFunctionGranted(fn))
            }
            listOf(
                    ProjectConfig::class.java,
                    ProjectAuthorisationMgt::class.java,
                    BranchCreate::class.java,
                    PromotionLevelCreate::class.java,
                    PromotionLevelEdit::class.java,
                    ValidationStampCreate::class.java,
                    ValidationStampEdit::class.java,
                    ProjectView::class.java,
                    BuildCreate::class.java,
                    BuildConfig::class.java,
                    PromotionRunCreate::class.java,
                    ValidationRunCreate::class.java
            ).forEach { fn ->
                assertTrue(securityService.isProjectFunctionGranted(project, fn))
            }
        }
    }

    @Test
    fun `Project role functions are inherited`() {
        val project = doCreateProject()
        asAccount(doCreateAccountWithProjectRole(project, extProjectRole)).call {
            // Specific functions
            assertFalse(securityService.isGlobalFunctionGranted(TestGlobalFunction::class.java))
            assertTrue(securityService.isProjectFunctionGranted(project, TestProject1Function::class.java))
            assertFalse(securityService.isProjectFunctionGranted(project, TestProject2Function::class.java))
            // Inherited functions
            listOf(
                    ProjectView::class.java,
                    ValidationRunStatusChange::class.java
            ).forEach { fn ->
                assertTrue(securityService.isProjectFunctionGranted(project, fn))
            }
        }
    }

}