package net.nemerosa.ontrack.job.orchestrator

import net.nemerosa.ontrack.job.JobRegistration
import org.springframework.stereotype.Component
import java.util.stream.Stream

@Component
class NOPJobOrchestratorSupplier : JobOrchestratorSupplier {
    override fun collectJobRegistrations(): Stream<JobRegistration> = Stream.empty()
}