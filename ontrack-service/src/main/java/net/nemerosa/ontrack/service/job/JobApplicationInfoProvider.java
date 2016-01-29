package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.job.JobRunProgress;
import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.job.JobStatus;
import net.nemerosa.ontrack.model.support.ApplicationInfo;
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobApplicationInfoProvider implements ApplicationInfoProvider {

    private final JobScheduler jobScheduler;

    @Autowired
    public JobApplicationInfoProvider(JobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
    }

    @Override
    public List<ApplicationInfo> getApplicationInfoList() {
        return jobScheduler.getJobStatuses().stream()
                .map(this::getApplicationInfo)
                .filter(info -> info != null)
                .collect(Collectors.toList());
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
