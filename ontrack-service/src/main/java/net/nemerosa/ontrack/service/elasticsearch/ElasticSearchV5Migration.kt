package net.nemerosa.ontrack.service.elasticsearch

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.SearchService
import net.nemerosa.ontrack.model.support.StartupService
import net.nemerosa.ontrack.model.support.StorageService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * When migrating to V5, we need to remove all the indexes and start an indexation from scratch.
 */
@Component
class ElasticSearchV5Migration(
    private val storageService: StorageService,
    private val searchService: SearchService,
    private val securityService: SecurityService,
) : StartupService {

    private val logger: Logger = LoggerFactory.getLogger(ElasticSearchV5Migration::class.java)

    override fun getName(): String? = "ElasticSearch V5 migration"

    /**
     * Runs after the jobs have been registered.
     */
    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION + 1

    override fun start() {
        val migrated = storageService
            .find(store, KEY, ElasticSearchV5MigrationStatus::class)
            ?.migrated ?: false
        if (!migrated) {
            logger.info("Removing all ElasticSearch indexes for migration to V5 (ES9)...")
            securityService.asAdmin {
                searchService.indexReset(reindex = true)
            }
            logger.info("Removed and repopulated all ElasticSearch indexes for migration to V5 (ES9).")
            storageService.store(
                store = store,
                key = KEY,
                data = ElasticSearchV5MigrationStatus(migrated = true)
            )
        } else {
            logger.info("ElasticSearch indexes have already been migrated to V5 (ES9).")
        }
    }

    companion object {
        private val store: String = ElasticSearchV5Migration::class.java.name
        private const val KEY = "migration"
    }

    data class ElasticSearchV5MigrationStatus(val migrated: Boolean)
}