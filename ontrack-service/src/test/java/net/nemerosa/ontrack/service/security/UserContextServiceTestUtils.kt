package net.nemerosa.ontrack.service.security

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.AccountRepository
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class UserContextServiceTestUtils(
    private val accountRepository: AccountRepository,
    private val accountService: AccountService,
) {

    fun withNoSecurityContext(
        code: () -> Unit,
    ) {
        val oldContext = SecurityContextHolder.getContext()
        SecurityContextHolder.clearContext()
        try {
            code()
        } finally {
            SecurityContextHolder.setContext(oldContext)
        }
    }

    fun <T> withAdminSecurityContext(
        code: () -> T
    ): T =
        withAccountSecurityContext(
            account = accountRepository.getAccount(ID.of(1)),
            code = code,
        )

    fun <T> withGlobalRoleUserSecurityContext(
        globalRole: String,
        code: () -> T
    ): T {
        val account = createUser {
            accountService.saveGlobalPermission(
                PermissionTargetType.ACCOUNT,
                it.id(),
                PermissionInput(role = globalRole)
            )
        }
        return withAccountSecurityContext(
            account = account,
            code = code,
        )
    }

    fun <T> withProjectRoleUserSecurityContext(
        project: Project,
        projectRole: String,
        code: () -> T
    ): T {
        val account = createUser {
            accountService.saveProjectPermission(
                project.id,
                PermissionTargetType.ACCOUNT,
                it.id(),
                PermissionInput(role = projectRole)
            )
        }
        return withAccountSecurityContext(
            account = account,
            code = code,
        )
    }

    fun createUser(
        name: String = uid("user-"),
        fullName: String = "User $name",
        email: String = "$name@test.com",
        password: String = "test",
        code: (account: Account) -> Unit,
    ): Account = withAdminSecurityContext {
        accountService.create(
            AccountInput(
                name = name,
                fullName = fullName,
                email = email,
                password = password,
                groups = emptyList(),
                disabled = false,
                locked = false,
            )
        ).apply {
            code(this)
        }
    }

    fun <T> withAccountSecurityContext(
        account: Account,
        code: () -> T
    ): T {
        val adminContext = mockSecurityContext(
            account = account
        )
        return withSecurityContext(adminContext, code)
    }

    private fun mockSecurityContext(
        account: Account,
    ): SecurityContext {
        val authentication = mockAuthentication(account)

        val context = mockk<SecurityContext>()
        every { context.authentication } returns authentication

        return context
    }

    private fun mockAuthentication(
        account: Account,
    ): Authentication {
        val principal = createPrincipal(account)

        val authentication = mockk<Authentication>()
        every { authentication.principal } returns principal
        every { authentication.isAuthenticated } returns true

        return authentication
    }

    private fun createPrincipal(account: Account): OntrackAuthenticatedUser {
        return accountService.withACL(
            AccountOntrackUser(account)
        )
    }

    private fun <T> withSecurityContext(
        context: SecurityContext,
        code: () -> T,
    ): T {
        val oldContext = SecurityContextHolder.getContext()
        SecurityContextHolder.setContext(context)
        return try {
            code()
        } finally {
            SecurityContextHolder.setContext(oldContext)
        }
    }

}