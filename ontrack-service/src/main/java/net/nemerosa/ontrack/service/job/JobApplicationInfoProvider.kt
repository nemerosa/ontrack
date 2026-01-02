package net.nemerosa.ontrack.service.job

import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.job.JobStatus
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ApplicationInfo
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider
import org.springframework.stereotype.Component

@Component
class JobApplicationInfoProvider(
    private val jobScheduler: JobScheduler,
    private val securityService: SecurityService
) : ApplicationInfoProvider {

    override val applicationInfoList: List<ApplicationInfo>
        get() = securityService.asAdmin {
            jobScheduler.getJobStatuses()
                .mapNotNull { status -> getApplicationInfo(status) }
        }

    private fun getApplicationInfo(status: JobStatus): ApplicationInfo? {
        val progress = status.progress
        if (status.isRunning && progress != null) {
            return ApplicationInfo.info(progress.text)
        } else {
            return null
        }
    }
}
