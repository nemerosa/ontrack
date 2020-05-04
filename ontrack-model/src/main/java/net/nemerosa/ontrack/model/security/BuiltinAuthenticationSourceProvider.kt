package net.nemerosa.ontrack.model.security

import org.springframework.stereotype.Component

@Component
class BuiltinAuthenticationSourceProvider : AuthenticationSourceProvider {

    companion object {

        const val ID = "built-in"

        val SOURCE = AuthenticationSource(
                provider = ID,
                key = "built-in",
                name = "Built-in",
                isEnabled = true,
                isAllowingPasswordChange = true
        )

    }

    override val id: String = ID

    override val sources = listOf(SOURCE)

}