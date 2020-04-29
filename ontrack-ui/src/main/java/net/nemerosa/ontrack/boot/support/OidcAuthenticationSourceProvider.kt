package net.nemerosa.ontrack.boot.support

import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import org.springframework.stereotype.Component

// TODO Move to its own extension
@Component
class OidcAuthenticationSourceProvider : AuthenticationSourceProvider {

    override val source: AuthenticationSource = SOURCE

    override val isEnabled: Boolean
        get() = TODO("Must check that at least one OIDC provider is enabled")

    companion object {
        val SOURCE = AuthenticationSource("oidc", "OIDC", isAllowingPasswordChange = false, isGroupMappingSupported = true)
    }

}