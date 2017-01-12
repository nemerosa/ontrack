package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.model.support.ApplicationInfo;
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class JobSchedulerApplicationInfoProvider implements ApplicationInfoProvider {

    private final JobScheduler jobScheduler;

    @Autowired
    public JobSchedulerApplicationInfoProvider(JobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
    }

    @Override
    public List<ApplicationInfo> getApplicationInfoList() {
        return (jobScheduler.isPaused()) ?
                Collections.singletonList(
                        ApplicationInfo.warning(
                                "Execution of background jobs is paused."
                        )
                ) :
                Collections.emptyList();
    }
}
