package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.job.JobPortal;
import net.nemerosa.ontrack.model.support.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobPortalStartupService implements StartupService {

    private final JobPortal portal;

    @Autowired
    public JobPortalStartupService(JobPortal portal) {
        this.portal = portal;
    }

    @Override
    public String getName() {
        return "Job portal startup";
    }

    @Override
    public int startupOrder() {
        return 5;
    }

    @Override
    public void start() {
        portal.fire();
    }
}
