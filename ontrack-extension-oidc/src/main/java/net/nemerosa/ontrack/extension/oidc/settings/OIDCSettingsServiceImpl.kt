package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.DocumentsRepository
import net.nemerosa.ontrack.model.support.StorageService
import net.nemerosa.ontrack.model.support.retrieve
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentHashMap

@Service
@Transactional
class OIDCSettingsServiceImpl(
        private val securityService: SecurityService,
        private val storageService: StorageService,
        private val encryptionService: EncryptionService,
        private val documentsRepository: DocumentsRepository
) : OIDCSettingsService {

    /**
     * Provider listeners
     */
    private val providerListeners = mutableListOf<OIDCProviderListener>()

    /**
     * Settings listeners
     */
    private val listeners = mutableListOf<OIDCSettingsListener>()

    /**
     * Caching the list of providers
     */
    private val providersCache = ConcurrentHashMap<String, List<OntrackOIDCProvider>>()

    override val cachedProviders: List<OntrackOIDCProvider>
        get() = providersCache.getOrPut(CACHE_KEY) {
            providers
        }

    override val cachedProviderNames: List<NameDescription>
        get() = securityService.asAdmin {
            cachedProviders.map {
                NameDescription(it.id, it.name)
            }
        }

    override val providers: List<OntrackOIDCProvider>
        get() {
            securityService.checkGlobalFunction(GlobalSettings::class.java)
            return storageService.getData(OIDC_PROVIDERS_STORE, StoredOntrackOIDCProvider::class.java)
                    .values
                    .sortedBy { it.id }
                    .map { decrypt(it) }
        }

    override fun createProvider(input: OntrackOIDCProvider): OntrackOIDCProvider {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        if (storageService.exists(OIDC_PROVIDERS_STORE, input.id)) {
            throw OntrackOIDCProviderIDAlreadyExistsException(input.id)
        } else {
            storageService.store(OIDC_PROVIDERS_STORE, input.id, encrypt(input))
            clearCache()
            return input
        }
    }

    override fun deleteProvider(id: String): Ack {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        val provider = getProviderById(id)
        return if (provider != null) {
            // Cleanup
            cleanupForProvider(provider)
            // Actual deletion
            storageService.delete(OIDC_PROVIDERS_STORE, id)
            clearCache()
            Ack.OK
        } else {
            Ack.NOK
        }
    }

    /**
     * Cleanup of accounts and group mappings.
     */
    private fun cleanupForProvider(provider: OntrackOIDCProvider) {
        providerListeners.forEach { it.onOIDCProviderDeleted(provider) }
    }

    override fun getProviderById(id: String): OntrackOIDCProvider? {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        return storageService.retrieve<StoredOntrackOIDCProvider>(OIDC_PROVIDERS_STORE, id)?.let {
            decrypt(it)
        }
    }

    override fun updateProvider(input: OntrackOIDCProvider): OntrackOIDCProvider {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        val existing = storageService.retrieve<StoredOntrackOIDCProvider>(OIDC_PROVIDERS_STORE, input.id)
                ?.let { decrypt(it) }
                ?: throw OntrackOIDCProviderIDNotFoundException(input.id)
        val record = OntrackOIDCProvider(
                id = input.id,
                name = input.name,
                description = input.description,
                issuerId = input.issuerId,
                clientId = input.clientId,
                clientSecret = if (input.clientSecret.isNotBlank()) {
                    input.clientSecret
                } else {
                    existing.clientSecret
                },
                groupFilter = input.groupFilter
        )
        storageService.store(OIDC_PROVIDERS_STORE, input.id, encrypt(record))
        clearCache()
        return record
    }

    override fun getProviderImage(id: String): Document? {
        checkProviderId(id)
        return documentsRepository.loadDocument(OIDC_PROVIDERS_IMAGES, id)
    }

    override fun hasProviderImage(id: String): Boolean {
        checkProviderId(id)
        return documentsRepository.documentExists(OIDC_PROVIDERS_IMAGES, id)
    }

    override fun setProviderImage(id: String, image: Document?) {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        checkProviderId(id)
        if (image != null) {
            documentsRepository.storeDocument(OIDC_PROVIDERS_IMAGES, id, image)
        } else {
            documentsRepository.deleteDocument(OIDC_PROVIDERS_IMAGES, id)
        }
    }

    private fun checkProviderId(id: String) {
        if (!storageService.exists(OIDC_PROVIDERS_STORE, id)) {
            throw OntrackOIDCProviderIDNotFoundException(id)
        }
    }

    private fun clearCache() {
        providersCache.clear()
        notifySettingsListeners()
    }

    private fun notifySettingsListeners() {
        listeners.forEach {
            it.onOidcSettingsChanged()
        }
    }

    override fun addOidcSettingsListener(listener: OIDCSettingsListener) {
        listeners += listener
    }

    override fun addOidcProviderListener(oidcProviderListener: OIDCProviderListener) {
        providerListeners += oidcProviderListener
    }

    companion object {
        /**
         * Cache unique key
         */
        private const val CACHE_KEY = "0"

        /**
         * Name of the store for the storage of the data
         */
        private val OIDC_PROVIDERS_STORE = OntrackOIDCProvider::class.java.name

        /**
         * Name of the store for the images
         */
        private val OIDC_PROVIDERS_IMAGES = OntrackOIDCProvider::class.java.name
    }

    /**
     * Stored object
     */
    private data class StoredOntrackOIDCProvider(
            val id: String,
            val name: String,
            val description: String,
            val issuerId: String,
            val clientId: String,
            val clientEncryptedSecret: String,
            val groupFilter: String?
    )

    private fun decrypt(stored: StoredOntrackOIDCProvider) = OntrackOIDCProvider(
            id = stored.id,
            name = stored.name,
            description = stored.description,
            issuerId = stored.issuerId,
            clientId = stored.clientId,
            clientSecret = encryptionService.decrypt(stored.clientEncryptedSecret) ?: "",
            groupFilter = stored.groupFilter
    )

    private fun encrypt(input: OntrackOIDCProvider) = StoredOntrackOIDCProvider(
            id = input.id,
            name = input.name,
            description = input.description,
            issuerId = input.issuerId,
            clientId = input.clientId,
            clientEncryptedSecret = (encryptionService.encrypt(input.clientSecret)
                    ?: throw OntrackOIDCProviderCannotEncryptSecretException()),
            groupFilter = input.groupFilter
    )
}