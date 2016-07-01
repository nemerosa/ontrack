package net.nemerosa.ontrack.job.orchestrator;

import net.nemerosa.ontrack.job.JobRegistration;

import java.util.stream.Stream;

@FunctionalInterface
public interface JobOrchestratorSupplier {

    Stream<JobRegistration> collectJobRegistrations();

}
