package net.nemerosa.ontrack.model.job;

import java.util.Optional;
import java.util.concurrent.Future;

@FunctionalInterface
public interface JobConsumer {

    /**
     * Tries to schedule a job. If successful, returns a future which defines the progress of the job. If not,
     * returns empty.
     *
     * @param job Job to schedule
     * @return Future or empty
     */
    Optional<Future<?>> accept(Job job);

}
