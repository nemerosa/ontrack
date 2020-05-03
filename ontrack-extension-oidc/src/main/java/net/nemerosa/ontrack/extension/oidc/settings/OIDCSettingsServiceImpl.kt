package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import net.nemerosa.ontrack.model.support.retrieve
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OIDCSettingsServiceImpl(
        private val securityService: SecurityService,
        private val storageService: StorageService
) : OIDCSettingsService {

    override val providers: List<OntrackOIDCProvider>
        get() {
            securityService.checkGlobalFunction(GlobalSettings::class.java)
            return storageService.getData(OIDC_PROVIDERS_STORE, OntrackOIDCProvider::class.java)
                    .values
                    .sortedBy { it.id }
        }

    override fun createProvider(input: OntrackOIDCProvider): OntrackOIDCProvider {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        val existing = storageService.retrieve<OntrackOIDCProvider>(OIDC_PROVIDERS_STORE, input.id)
        if (existing != null) {
            throw OntrackOIDCProviderIDAlreadyExistsException(input.id)
        } else {
            storageService.store(OIDC_PROVIDERS_STORE, input.id, input)
            return input
        }
    }

    companion object {
        /**
         * Name of the store
         */
        private val OIDC_PROVIDERS_STORE = OntrackOIDCProvider::class.java.name
    }
}