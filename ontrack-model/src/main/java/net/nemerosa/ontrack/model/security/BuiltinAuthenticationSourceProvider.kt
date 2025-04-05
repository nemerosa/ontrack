package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SecuritySettings
import org.springframework.stereotype.Component

@Component
@Deprecated("Will be removed in V5")
class BuiltinAuthenticationSourceProvider(
    private val cachedSettingsService: CachedSettingsService,
) : AuthenticationSourceProvider {

    companion object {

        const val ID = "built-in"

        /**
         * Private authentication source used by the run-as service
         */
        val runAsSource = AuthenticationSource(
            provider = ID,
            key = "",
            name = "Built-in run-as authentication",
            isEnabled = true,
            isAllowingPasswordChange = false
        )

    }

    override val id: String = ID

    val source: AuthenticationSource
        get() = cachedSettingsService.getCachedSettings(SecuritySettings::class.java).run {
            AuthenticationSource(
                provider = ID,
                key = "",
                name = "Built-in",
                isEnabled = builtInAuthenticationEnabled,
                isAllowingPasswordChange = true,
            )
        }

    override val sources get() = listOf(source)

}