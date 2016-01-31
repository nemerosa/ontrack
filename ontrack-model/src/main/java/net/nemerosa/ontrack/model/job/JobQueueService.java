package net.nemerosa.ontrack.model.job;

import java.util.Optional;
import java.util.concurrent.Future;

public interface JobQueueService {

    /**
     * Schedules a job for run.
     *
     * @param job Job to run.
     * @return Returns a future describing the job progress if the job could be scheduled successfully, if not
     * returns empty.
     */
    Optional<Future<?>> queue(Job job);

}
