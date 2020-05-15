package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.AuthenticationSourceRepository
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import java.sql.ResultSet

fun AuthenticationSource.asParams() = MapSqlParameterSource(
        mapOf(
                "provider" to provider,
                "source" to key
        )
)

fun ResultSet.getAuthenticationSource(authenticationSourceRepository: AuthenticationSourceRepository): AuthenticationSource? {
    val provider: String = getString("PROVIDER")
    val source: String = getString("SOURCE")
    return authenticationSourceRepository.getAuthenticationSource(provider, source)
}
