package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.job.Job;
import net.nemerosa.ontrack.model.job.JobConsumer;
import net.nemerosa.ontrack.model.job.JobQueueAccessService;
import net.nemerosa.ontrack.model.job.JobQueueService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class JobQueueServiceImpl implements JobQueueService, JobQueueAccessService {

    private final Set<JobConsumer> consumers = Collections.synchronizedSet(new LinkedHashSet<>());

    @Override
    public void registerQueueListener(JobConsumer jobConsumer) {
        consumers.add(jobConsumer);
    }

    /**
     * At least one consumer must have accepted the job.
     */
    @Override
    public Ack queue(Job job) {
        boolean accepted = false;
        for (JobConsumer consumer : consumers) {
            accepted = accepted || consumer.accept(job);
        }
        return Ack.validate(accepted);
    }

}
