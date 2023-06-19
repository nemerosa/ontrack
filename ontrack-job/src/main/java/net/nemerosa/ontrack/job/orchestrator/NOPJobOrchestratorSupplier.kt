package net.nemerosa.ontrack.job.orchestrator

import net.nemerosa.ontrack.job.JobRegistration
import org.springframework.stereotype.Component

@Component
class NOPJobOrchestratorSupplier : JobOrchestratorSupplier {
    override val jobRegistrations: Collection<JobRegistration> = emptyList()
}