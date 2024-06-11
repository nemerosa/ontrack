package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.support.StartupService
import net.nemerosa.ontrack.model.support.StorageService
import net.nemerosa.ontrack.model.support.retrieve
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Assigning name to all existing subscriptions.
 */
@Component
class SubscriptionNameMigration(
    private val storageService: StorageService,
    private val globalSubscriptionStore: GlobalSubscriptionStore,
    private val entitySubscriptionStore: EntitySubscriptionStore,
): StartupService {

    private val logger = LoggerFactory.getLogger(SubscriptionNameMigration::class.java)

    override fun getName(): String = "Subscription names"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        // Checks if the migration was already done or not
        val migrated = storageService.find(SubscriptionNameMigration::class.java.name, "migration", SubscriptionNameMigrationStatus::class)?.migrated ?: false
        if (!migrated) {
            // Global subscriptions
            globalSubscriptionStore.migrateSubscriptionNames()
            // Entity subscriptions
            entitySubscriptionStore.migrateSubscriptionNames()
            // Setting the status
            storageService.store(SubscriptionNameMigration::class.java.name, "migration", SubscriptionNameMigrationStatus(migrated = true))
        } else {
            logger.info("Subscription names were already migrated")
        }
    }

    data class SubscriptionNameMigrationStatus(
        val migrated: Boolean,
    )
}