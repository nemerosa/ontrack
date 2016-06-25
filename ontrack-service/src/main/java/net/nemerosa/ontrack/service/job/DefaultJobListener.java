package net.nemerosa.ontrack.service.job;

import com.codahale.metrics.MetricRegistry;
import net.nemerosa.ontrack.job.JobKey;
import net.nemerosa.ontrack.job.JobListener;
import net.nemerosa.ontrack.job.JobRunProgress;
import net.nemerosa.ontrack.job.JobStatus;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.support.ApplicationLogEntry;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import net.nemerosa.ontrack.model.support.SettingsRepository;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

public class DefaultJobListener implements JobListener {

    private final ApplicationLogService logService;
    private final MetricRegistry metricRegistry;
    private final CounterService counterService;
    private final SettingsRepository settingsRepository;

    public DefaultJobListener(ApplicationLogService logService, MetricRegistry metricRegistry, CounterService counterService, SettingsRepository settingsRepository) {
        this.logService = logService;
        this.metricRegistry = metricRegistry;
        this.counterService = counterService;
        this.settingsRepository = settingsRepository;
    }

    protected String getJobTypeMetric(JobKey key) {
        return "job-type." + key.getType().getKey();
    }

    @Override
    public void onJobStart(JobKey key) {
        counterService.increment("job");
        counterService.increment(getJobTypeMetric(key));
    }

    @Override
    @Transactional
    public void onJobPaused(JobKey key) {
        settingsRepository.setBoolean(JobListener.class, key.toString(), true);
    }

    @Override
    @Transactional
    public void onJobResumed(JobKey key) {
        settingsRepository.delete(JobListener.class, key.toString());
    }

    @Override
    public void onJobEnd(JobKey key, long milliseconds) {
        metricRegistry.timer("job").update(milliseconds, TimeUnit.MILLISECONDS);
        metricRegistry.timer(getJobTypeMetric(key)).update(milliseconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onJobError(JobStatus status, Exception ex) {
        JobKey key = status.getKey();
        counterService.increment("error.job");
        counterService.increment("error." + getJobTypeMetric(status.getKey()));
        logService.log(
                ApplicationLogEntry.error(
                        ex,
                        NameDescription.nd(
                                key.getType().toString(),
                                key.getType().getName()
                        ),
                        status.getDescription()
                ).withDetail("job.key", key.getId())
                        .withDetail("job.progress", status.getProgressText())
        );
    }

    @Override
    public void onJobComplete(JobKey key) {
        counterService.decrement("job");
        counterService.decrement(getJobTypeMetric(key));
    }

    @Override
    public void onJobProgress(JobKey key, JobRunProgress progress) {
    }

    @Override
    public boolean isPausedAtStartup(JobKey key) {
        return settingsRepository.getBoolean(
                JobListener.class,
                key.toString(),
                false
        );
    }
}
