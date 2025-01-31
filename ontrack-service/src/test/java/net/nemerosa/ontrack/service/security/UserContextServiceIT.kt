package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserContextServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var utils: UserContextServiceTestUtils

    @Test
    fun `No user context when no security context`() {
        utils.withNoSecurityContext {
            assertFailsWith<NoUserContextException> {
                userContextService.currentSpringSecurityContextToUserContext()
            }
        }
    }

    @Test
    fun `User context for admin user`() {
        utils.withAdminSecurityContext {
            val ctx = userContextService.currentSpringSecurityContextToUserContext()
            assertEquals(1, ctx.id)
            assertEquals("admin", ctx.name)
            assertEquals("", ctx.email)
            assertEquals("Administrator", ctx.fullName)
        }
    }

    @Test
    fun `Checking global functions for builtin admin`() {
        utils.withAdminSecurityContext {
            val ctx = userContextService.currentSpringSecurityContextToUserContext()
            rolesService.globalFunctions.forEach { fn ->
                assertTrue(
                    ctx.isGlobalFunctionGranted(fn.kotlin),
                    "$fn is granted to admin user"
                )
            }
        }
    }

    @Test
    fun `Checking global functions for a user`() {
        utils.withGlobalRoleUserSecurityContext(
            globalRole = Roles.GLOBAL_READ_ONLY,
        ) {
            val ctx = userContextService.currentSpringSecurityContextToUserContext()
            assertTrue(
                ctx.isGlobalFunctionGranted<ProjectList>(),
                "Read only is granted to read-only user"
            )
            assertFalse(
                ctx.isGlobalFunctionGranted<ProjectCreation>(),
                "Project creation is not granted to read-only user"
            )
        }
    }

    @Test
    fun `Checking project functions for builtin admin`() {
        project {
            utils.withAdminSecurityContext {
                val ctx = userContextService.currentSpringSecurityContextToUserContext()
                rolesService.projectFunctions.forEach { fn ->
                    assertTrue(
                        ctx.isProjectFunctionGranted(id(), fn.kotlin),
                        "$fn is granted to admin user on project"
                    )
                }
            }
        }
    }

    @Test
    fun `Checking project functions for a user`() {
        project {
            utils.withProjectRoleUserSecurityContext(
                project = this,
                projectRole = Roles.PROJECT_PARTICIPANT
            ) {
                val ctx = userContextService.currentSpringSecurityContextToUserContext()
                assertFalse(
                    ctx.isProjectFunctionGranted<ProjectEdit>(project),
                    "Project edition is not granted to participant"
                )
                assertTrue(
                    ctx.isProjectFunctionGranted<ValidationRunStatusCommentEditOwn>(project),
                    "Validation run comment is granted to participant"
                )
            }
        }
    }

}