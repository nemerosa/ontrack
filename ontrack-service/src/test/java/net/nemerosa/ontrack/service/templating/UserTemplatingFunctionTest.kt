package net.nemerosa.ontrack.service.templating

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AuthenticatedUser
import net.nemerosa.ontrack.model.security.SecurityService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UserTemplatingFunctionTest {

    private lateinit var account: Account
    private lateinit var user: AuthenticatedUser
    private lateinit var securityService: SecurityService
    private lateinit var userTemplatingFunction: UserTemplatingFunction

    @BeforeEach
    fun setUp() {
        account = mockk()

        user = mockk()
        every { user.account } returns account

        securityService = mockk()
        userTemplatingFunction = UserTemplatingFunction(securityService)
    }

    @Test
    fun `No account`() {
        every { securityService.currentUser } returns null
        assertEquals(
            "",
            userTemplatingFunction.render(
                configMap = emptyMap(),
                context = emptyMap(),
                renderer = PlainEventRenderer.INSTANCE,
                expressionResolver = { it }
            )
        )
    }

    @Test
    fun `Account username field by default`() {
        every { account.name } returns "test"
        every { securityService.currentUser } returns user
        assertEquals(
            "test",
            userTemplatingFunction.render(
                configMap = emptyMap(),
                context = emptyMap(),
                renderer = PlainEventRenderer.INSTANCE,
                expressionResolver = { it }
            )
        )
    }

    @Test
    fun `Account username field`() {
        every { account.name } returns "test"
        every { securityService.currentUser } returns user
        assertEquals(
            "test",
            userTemplatingFunction.render(
                configMap = mapOf(
                    "field" to "name"
                ),
                context = emptyMap(),
                renderer = PlainEventRenderer.INSTANCE,
                expressionResolver = { it }
            )
        )
    }

    @Test
    fun `Account display name field`() {
        every { account.fullName } returns "User Test"
        every { securityService.currentUser } returns user
        assertEquals(
            "User Test",
            userTemplatingFunction.render(
                configMap = mapOf(
                    "field" to "display"
                ),
                context = emptyMap(),
                renderer = PlainEventRenderer.INSTANCE,
                expressionResolver = { it }
            )
        )
    }

    @Test
    fun `Account email field`() {
        every { account.email } returns "user@test.com"
        every { securityService.currentUser } returns user
        assertEquals(
            "user@test.com",
            userTemplatingFunction.render(
                configMap = mapOf(
                    "field" to "email"
                ),
                context = emptyMap(),
                renderer = PlainEventRenderer.INSTANCE,
                expressionResolver = { it }
            )
        )
    }

}