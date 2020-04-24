package net.nemerosa.ontrack.model.security

import org.springframework.stereotype.Component

@Component
class BuiltinAuthenticationSourceProvider : AuthenticationSourceProvider {

    companion object {
        const val ID = "password"

        val SOURCE = AuthenticationSource.of(
                ID,
                "Built-in"
        ).withAllowingPasswordChange(true)
    }

    override val source = SOURCE

}