package net.nemerosa.ontrack.model.support;

import org.springframework.scheduling.Trigger;

public interface ScheduledService {

    Runnable getTask();

    Trigger getTrigger();

}
