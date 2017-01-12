package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.job.JobRunProgress;
import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.job.JobStatus;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ApplicationInfo;
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JobApplicationInfoProvider implements ApplicationInfoProvider {

    private final JobScheduler jobScheduler;
    private final SecurityService securityService;

    @Autowired
    public JobApplicationInfoProvider(JobScheduler jobScheduler, SecurityService securityService) {
        this.jobScheduler = jobScheduler;
        this.securityService = securityService;
    }

    @Override
    public List<ApplicationInfo> getApplicationInfoList() {
        return securityService.asAdmin(() -> jobScheduler.getJobStatuses().stream()
                .map(this::getApplicationInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
        );
    }

    private ApplicationInfo getApplicationInfo(JobStatus status) {
        JobRunProgress progress = status.getProgress();
        if (status.isRunning() && progress != null) {
            return ApplicationInfo.info(progress.getText());
        } else {
            return null;
        }
    }
}
