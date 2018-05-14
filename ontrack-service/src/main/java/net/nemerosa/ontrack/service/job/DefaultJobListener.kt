package net.nemerosa.ontrack.service.job

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.job.JobKey
import net.nemerosa.ontrack.job.JobListener
import net.nemerosa.ontrack.job.JobRunProgress
import net.nemerosa.ontrack.job.JobStatus
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

open class DefaultJobListener(
        private val logService: ApplicationLogService,
        private val meterRegistry: MeterRegistry,
        private val settingsRepository: SettingsRepository
) : JobListener {

    override fun onJobStart(key: JobKey) {
    }

    @Transactional
    override fun onJobPaused(key: JobKey) {
        settingsRepository.setBoolean(JobListener::class.java, key.toString(), true)
    }

    @Transactional
    override fun onJobResumed(key: JobKey) {
        settingsRepository.delete(JobListener::class.java, key.toString())
    }

    override fun onJobEnd(key: JobKey, milliseconds: Long) {
        meterRegistry.timer("ontrack_job_duration_ms", key.metricTags).record(milliseconds, TimeUnit.MILLISECONDS)
        meterRegistry.counter("ontrack_job_run_count", key.metricTags).increment()
    }

    override fun onJobError(status: JobStatus, ex: Exception) {
        val key = status.key
        meterRegistry.counter("ontrack_job_error_count", key.metricTags).increment()
        logService.log(
                ApplicationLogEntry.error(
                        ex,
                        NameDescription.nd(
                                key.type.toString(),
                                key.type.name
                        ),
                        status.description
                ).withDetail("job.key", key.id)
                        .withDetail("job.progress", status.progressText)
        )
    }

    override fun onJobComplete(key: JobKey) {
    }

    override fun onJobProgress(key: JobKey, progress: JobRunProgress) {}

    override fun isPausedAtStartup(key: JobKey): Boolean {
        return settingsRepository.getBoolean(
                JobListener::class.java,
                key.toString(),
                false
        )
    }
}
