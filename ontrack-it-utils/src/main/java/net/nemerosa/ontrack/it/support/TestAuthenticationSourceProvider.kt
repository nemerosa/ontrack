package net.nemerosa.ontrack.it.support

import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import org.springframework.stereotype.Component

@Component
class TestAuthenticationSourceProvider : AuthenticationSourceProvider {

    companion object {

        const val ID = "test"

        val SOURCE = AuthenticationSource(
            provider = ID,
            key = "",
            name = "Test",
            isEnabled = true,
            isAllowingPasswordChange = true,
            isGroupMappingSupported = true,
        )

    }

    override val id: String = ID

    override val sources = listOf(SOURCE)

}