package net.nemerosa.ontrack.service.job

import net.nemerosa.ontrack.job.JobRegistration
import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.job.orchestrator.JobOrchestrator
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.support.JobProvider
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class JobOrchestrationConfig(
    private val configProperties: OntrackConfigProperties,
    private val jobScheduler: JobScheduler,
    private val platformTransactionManager: PlatformTransactionManager,
    @Autowired(required = false)
    private val jobOrchestratorSuppliers: Collection<JobOrchestratorSupplier>?
) {

    @Bean
    fun jobOrchestrator() =
        JobOrchestrator(
            jobScheduler,
            "Collection of jobs",
            jobOrchestratorSuppliers ?: emptyList(),
            platformTransactionManager
        )

    @Bean
    fun jobOrchestratorRegistration(): JobProvider = JobProvider {
        listOf(
            JobRegistration(
                jobOrchestrator(),
                Schedule.everyMinutes(configProperties.jobs.orchestration.toLong())
            )
        )
    }

}
