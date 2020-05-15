package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.extension.oidc.OidcAuthenticationSourceProvider
import net.nemerosa.ontrack.model.security.AccountService
import org.springframework.stereotype.Component

@Component
class OIDCProviderCleanupListener(
        oidcSettingsService: OIDCSettingsService,
        private val accountService: AccountService
) : OIDCProviderListener {

    init {
        oidcSettingsService.addOidcProviderListener(this)
    }

    override fun onOIDCProviderDeleted(provider: OntrackOIDCProvider) {
        accountService.deleteAccountBySource(
                OidcAuthenticationSourceProvider.asSource(provider)
        )
    }
}