package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.extension.oidc.OidcAuthenticationSourceProvider
import net.nemerosa.ontrack.model.security.AccountGroupMappingService
import net.nemerosa.ontrack.model.security.AccountService
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OIDCProviderCleanupListener(
        oidcSettingsService: OIDCSettingsService,
        private val accountService: AccountService,
        private val accountGroupMappingService: AccountGroupMappingService
) : OIDCProviderListener {

    init {
        oidcSettingsService.addOidcProviderListener(this)
    }

    override fun onOIDCProviderDeleted(provider: OntrackOIDCProvider) {
        val source = OidcAuthenticationSourceProvider.asSource(provider)
        accountService.deleteAccountBySource(source)
        accountGroupMappingService.deleteMappingsBySource(source)
    }
}