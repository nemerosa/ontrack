package net.nemerosa.ontrack.service.job

import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.JobProvider
import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

/**
 * Starting the jobs at startup.
 */
@Component
class JobStartup(
    private val jobScheduler: JobScheduler,
    applicationContext: ApplicationContext,
    private val securityService: SecurityService
) : StartupService {

    private val jobProviders: Collection<JobProvider> =
        applicationContext.getBeansOfType(JobProvider::class.java).values

    override fun getName(): String = "Job registration at startup"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        securityService.asAdmin {
            jobProviders
                .flatMap { jobProvider -> jobProvider.startingJobs }
                .forEach { jobRegistration ->
                    jobScheduler.schedule(
                        jobRegistration.job,
                        jobRegistration.schedule
                    )
                }
        }
    }
}
