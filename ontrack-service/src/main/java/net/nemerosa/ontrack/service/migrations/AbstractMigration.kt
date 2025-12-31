package net.nemerosa.ontrack.service.migrations

import net.nemerosa.ontrack.model.support.StartupService
import net.nemerosa.ontrack.model.support.StorageService
import org.slf4j.LoggerFactory

/**
 * Base class for non-idempotent migrations whose execution must be recorded.
 */
abstract class AbstractMigration(
    private val storageService: StorageService,
) : StartupService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Performing the actual migration
     */
    abstract fun migration()

    final override fun start() {
        logger.info("Checking migration [$name]...")
        val migrated = storageService.find(
            store = this::class.java.name,
            key = "migration",
            type = MigrationRecord::class
        )?.migrated ?: false
        if (!migrated) {
            logger.info("Running migration [$name]...")
            migration()
            storageService.store(
                store = this::class.java.name,
                key = "migration",
                data = MigrationRecord(migrated = true)
            )
        } else {
            logger.info("Migration [$name] was already done.")
        }
    }

    private data class MigrationRecord(
        val migrated: Boolean,
    )
}