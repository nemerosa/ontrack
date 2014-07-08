package net.nemerosa.ontrack.service.job;

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

    @Override
    public void queue(Job job) {
        consumers.forEach(consumer -> consumer.accept(job));
    }

}
