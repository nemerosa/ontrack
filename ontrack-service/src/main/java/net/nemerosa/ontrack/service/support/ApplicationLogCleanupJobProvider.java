package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.job.*;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import net.nemerosa.ontrack.model.support.JobProvider;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
public class ApplicationLogCleanupJobProvider implements JobProvider, Job {

    private final OntrackConfigProperties configProperties;
    private final ApplicationLogService service;

    @Autowired
    public ApplicationLogCleanupJobProvider(OntrackConfigProperties configProperties, ApplicationLogService service) {
        this.configProperties = configProperties;
        this.service = service;
    }

    @Override
    public Collection<JobRegistration> getStartingJobs() {
        return Collections.singleton(
                JobRegistration.of(this).withSchedule(Schedule.EVERY_DAY)
        );
    }

    @Override
    public JobKey getKey() {
        return JobCategory.CORE.getType("application-log-cleanup").withName("Application log cleanup")
                .getKey("main");
    }

    @Override
    public JobRun getTask() {
        return runListener -> service.cleanup(configProperties.getApplicationLogRetentionDays());
    }

    @Override
    public String getDescription() {
        return "Cleanup of application logs after a given retention period";
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
