package net.nemerosa.ontrack.model.job;

public interface JobQueueAccessService {

    void registerQueueListener(JobConsumer jobConsumer);

}
