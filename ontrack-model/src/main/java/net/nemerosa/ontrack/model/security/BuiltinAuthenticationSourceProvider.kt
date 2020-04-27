package net.nemerosa.ontrack.model.security

import org.springframework.stereotype.Component

@Component
class BuiltinAuthenticationSourceProvider : AuthenticationSourceProvider {

    companion object {

        const val ID = "password"

        val SOURCE = AuthenticationSource(
                id = ID,
                name = "Built-in",
                isAllowingPasswordChange = true
        )

    }

    override val source = SOURCE

    override val isEnabled: Boolean = true
}