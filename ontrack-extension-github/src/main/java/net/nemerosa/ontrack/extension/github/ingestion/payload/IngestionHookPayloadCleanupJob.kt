package net.nemerosa.ontrack.extension.github.ingestion.payload

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.JobProvider
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class IngestionHookPayloadCleanupJob(
    private val cachedSettingsService: CachedSettingsService,
    private val ingestionHookPayloadStorage: IngestionHookPayloadStorage,
) : JobProvider {

    override fun getStartingJobs() = listOf(
        JobRegistration(
            createJob(),
            Schedule.EVERY_DAY
        )
    )

    private fun createJob() = object : Job {

        override fun getKey(): JobKey =
            JobCategory.of("github-ingestion").withName("GitHub Ingestion")
                .getType("github-ingestion-cleanup").withName("GitHub Ingestion Cleanup")
                .getKey("main")

        override fun getTask() = JobRun { listener ->
            val retentionDays =
                cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java).retentionDays
            if (retentionDays > 0) {
                val until: LocalDateTime = Time.now().minusDays(retentionDays.toLong())
                val deleted = ingestionHookPayloadStorage.cleanUntil(until)
                listener.message("$deleted ingestion hook payloads have been deleted.")
            }
        }

        override fun getDescription(): String = "Cleanup of past GitHub Ingestion payloads"

        override fun isDisabled(): Boolean = false
    }

}