package net.nemerosa.ontrack.model.job;

import net.nemerosa.ontrack.model.Ack;

public interface JobQueueService {

    /**
     * Schedules a job for run.
     *
     * @param job Job to run.
     * @return Returns {@link Ack#OK} if the job could be scheduling successfully,
     * or {@link Ack#NOK} if a job of the same {@link net.nemerosa.ontrack.model.job.Job#getGroup() group}
     * and {@link net.nemerosa.ontrack.model.job.Job#getId() id} was already running.
     */
    Ack queue(Job job);

}
