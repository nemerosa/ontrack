package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobListener;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultJobListener implements JobListener {

    private final ApplicationLogService logService;

    // FIXME Error management
    // TODO Metrics

    /**
     * String jobCategoryMetric = "job-category." + registeredJob.getJobCategory();
     * String jobMetric = "job";
     * Timer.Context jobTime = metricRegistry.timer(jobMetric).time();
     * Timer.Context jobCategoryTime = metricRegistry.timer(jobCategoryMetric).time();
     * try {
     * counterService.increment(jobMetric);
     * counterService.increment(jobCategoryMetric);
     * wrappedTask.run();
     * } finally {
     * counterService.decrement(jobMetric);
     * counterService.decrement(jobCategoryMetric);
     * jobTime.stop();
     * jobCategoryTime.stop();
     * }
     */

    @Autowired
    public DefaultJobListener(ApplicationLogService logService) {
        this.logService = logService;
    }

    @Override
    public void onJobStart(JobKey key) {
    }

    @Override
    public void onJobEnd(JobKey key, long milliseconds) {
    }

    @Override
    public void onJobError(JobKey key, Exception ex) {
//        logService.error(
//                ex,
//                getClass(),
//                String.format(
//                        "%s/%s",
//                        key.getType(),
//                        key.getId()
//                ),
//                registeredJob.getJobDescription(),
//                Optional.ofNullable(registeredJob.getApplicationInfo()).map(ApplicationInfo::getMessage).orElse(null)
//        );
    }

    @Override
    public void onJobComplete(JobKey key) {
    }
}
