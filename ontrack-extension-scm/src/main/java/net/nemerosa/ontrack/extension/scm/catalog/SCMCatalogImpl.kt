package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SCMCatalogImpl(
        private val storageService: StorageService,
        private val scmCatalogProviders: List<SCMCatalogProvider>
) : SCMCatalog {
    override fun collectSCMCatalog(logger: (String) -> Unit) {

        // Gets existing keys
        val keys = storageService.getKeys(SCM_CATALOG_STORE).toMutableSet()

        // Getting new & updated items
        scmCatalogProviders.forEach { provider ->
            logger("Collecting SCM Catalog for ${provider.id}")
            provider.entries.forEach { source ->
                logger("SCM Catalog entry: $source")
                // As entry
                val entry = SCMCatalogEntry(
                        scm = provider.id,
                        config = source.config,
                        repository = source.repository,
                        timestamp = Time.now(),
                        linked = true
                )
                // Stores the entry
                storageService.store(
                        SCM_CATALOG_STORE,
                        entry.key,
                        entry
                )
                // Stored
                keys.remove(entry.key)
            }
        }

        // Cleaning everything
        keys.forEach {
            storageService.delete(SCM_CATALOG_STORE, it)
        }
    }

    override val catalogEntries: Sequence<SCMCatalogEntry>
        get() = storageService.getData(SCM_CATALOG_STORE, SCMCatalogEntry::class.java).values.asSequence()
}

private const val SCM_CATALOG_STORE = "scm-catalog"
