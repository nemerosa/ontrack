package net.nemerosa.ontrack.service.job

import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.model.support.ApplicationInfo
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider
import org.springframework.stereotype.Component

@Component
class JobSchedulerApplicationInfoProvider(
    private val jobScheduler: JobScheduler
) : ApplicationInfoProvider {

    override val applicationInfoList: List<ApplicationInfo>
        get() = if (jobScheduler.isPaused()) {
            listOf(
                ApplicationInfo.warning("Execution of background jobs is paused.")
            )
        } else {
            emptyList()
        }
}
