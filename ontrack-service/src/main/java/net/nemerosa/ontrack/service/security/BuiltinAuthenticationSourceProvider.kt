package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AuthenticationSource.Companion.of
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import org.springframework.stereotype.Component

@Component
class BuiltinAuthenticationSourceProvider : AuthenticationSourceProvider {

    companion object {
        const val ID = "password"

        val SOURCE = of(
                ID,
                "Built-in"
        ).withAllowingPasswordChange(true)
    }

    override val source = SOURCE

}