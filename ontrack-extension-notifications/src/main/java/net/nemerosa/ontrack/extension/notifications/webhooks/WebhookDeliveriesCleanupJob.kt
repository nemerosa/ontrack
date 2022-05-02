package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component

@Component
class WebhookDeliveriesCleanupJob(
    private val cachedSettingsService: CachedSettingsService,
    private val webhookExchangeService: WebhookExchangeService,
) : JobProvider {
    override fun getStartingJobs() = listOf(
        JobRegistration(
            createWebhookDeliveriesCleanupJob(),
            Schedule.EVERY_DAY
        )
    )

    private fun createWebhookDeliveriesCleanupJob() = object : Job {

        override fun getKey(): JobKey = WebhookJobs.type.getKey("delivery-cleanup")

        override fun getTask() = JobRun {
            val settings = cachedSettingsService.getCachedSettings(WebhookSettings::class.java)
            webhookExchangeService.clearBefore(Time.now().minusDays(settings.deliveriesRetentionDays.toLong()))
        }

        override fun getDescription(): String = "Cleanup of webhook deliveries"

        override fun isDisabled(): Boolean = false
    }
}