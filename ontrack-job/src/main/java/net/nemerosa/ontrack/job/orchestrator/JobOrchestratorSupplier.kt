package net.nemerosa.ontrack.job.orchestrator

import net.nemerosa.ontrack.job.JobRegistration
import java.util.stream.Stream

interface JobOrchestratorSupplier {
    @Deprecated("Use jobRegistrations instead")
    fun collectJobRegistrations(): Stream<JobRegistration> = jobRegistrations.stream()

    val jobRegistrations: Collection<JobRegistration>
}
