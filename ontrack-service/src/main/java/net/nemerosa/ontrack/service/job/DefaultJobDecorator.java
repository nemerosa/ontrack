package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.job.Job;
import net.nemerosa.ontrack.job.JobDecorator;
import net.nemerosa.ontrack.model.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultJobDecorator implements JobDecorator {

    private final SecurityService securityService;

    @Autowired
    public DefaultJobDecorator(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public Runnable decorate(Job job, Runnable task) {
        return () -> securityService.asAdmin(task);
    }
}
