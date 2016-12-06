package net.nemerosa.ontrack.service.job;

import lombok.Data;
import net.nemerosa.ontrack.job.JobScheduler;
import net.nemerosa.ontrack.job.JobStatus;
import net.nemerosa.ontrack.model.metrics.OntrackMetrics;
import net.nemerosa.ontrack.model.metrics.OntrackTaggedMetrics;
import net.nemerosa.ontrack.model.metrics.TaggedMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JobMetrics implements OntrackMetrics, OntrackTaggedMetrics {

    private final JobScheduler scheduler;

    @Autowired
    public JobMetrics(JobScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Collection<Metric<?>> metrics() {
        // Gets the statuses
        Collection<JobStatus> statuses = scheduler.getJobStatuses();

        // Collects all metrics
        List<Metric<?>> metrics = new ArrayList<>();

        // Statuses
        JobMetric general = new JobMetric();
        Map<String, JobMetric> categories = new HashMap<>();
        for (JobStatus status : statuses) {
            general.count++;
            JobMetric.getCategory(categories, status).count++;
            if (status.isRunning()) {
                general.running++;
                JobMetric.getCategory(categories, status).running++;
            }
            if (status.isDisabled()) {
                general.disabled++;
                JobMetric.getCategory(categories, status).disabled++;
            }
            if (!status.isValid()) {
                general.invalid++;
                JobMetric.getCategory(categories, status).invalid++;
            }
            if (status.isPaused()) {
                general.paused++;
                JobMetric.getCategory(categories, status).paused++;
            }
            if (status.isError()) {
                general.error++;
                JobMetric.getCategory(categories, status).error++;
            }
        }

        // Collection of metrics
        metrics.add(new Metric<>("gauge.jobs", general.count));
        metrics.add(new Metric<>("gauge.jobs.running", general.running));
        metrics.add(new Metric<>("gauge.jobs.disabled", general.disabled));
        metrics.add(new Metric<>("gauge.jobs.error", general.error));
        metrics.add(new Metric<>("gauge.jobs.invalid", general.invalid));
        metrics.add(new Metric<>("gauge.jobs.paused", general.paused));

        // Per categories
        for (Map.Entry<String, JobMetric> entry : categories.entrySet()) {
            String category = entry.getKey();
            JobMetric metric = entry.getValue();
            metrics.add(new Metric<>("gauge.jobs." + category, metric.count));
            metrics.add(new Metric<>("gauge.jobs." + category + ".running", metric.running));
            metrics.add(new Metric<>("gauge.jobs." + category + ".disabled", metric.disabled));
            metrics.add(new Metric<>("gauge.jobs." + category + ".error", metric.error));
            metrics.add(new Metric<>("gauge.jobs." + category + ".invalid", metric.invalid));
            metrics.add(new Metric<>("gauge.jobs." + category + ".paused", metric.paused));
        }

        // OK
        return metrics;
    }

    @Override
    public Collection<TaggedMetric<?>> getTaggedMetrics() {
        // Gets the statuses
        Collection<JobStatus> statuses = scheduler.getJobStatuses();

        // Collects all metrics
        List<TaggedMetric<?>> metrics = new ArrayList<>();

        // Statuses
        Map<String, JobMetric> categories = new HashMap<>();
        for (JobStatus status : statuses) {
            JobMetric.getCategory(categories, status).count++;
            if (status.isRunning()) {
                JobMetric.getCategory(categories, status).running++;
            }
            if (status.isDisabled()) {
                JobMetric.getCategory(categories, status).disabled++;
            }
            if (!status.isValid()) {
                JobMetric.getCategory(categories, status).invalid++;
            }
            if (status.isPaused()) {
                JobMetric.getCategory(categories, status).paused++;
            }
            if (status.isError()) {
                JobMetric.getCategory(categories, status).error++;
            }
        }

        // Per categories
        for (Map.Entry<String, JobMetric> entry : categories.entrySet()) {
            String category = entry.getKey();
            JobMetric metric = entry.getValue();
            metrics.add(categoryMetric("ontrack.job.count", category, metric.count));
            metrics.add(categoryMetric("ontrack.job.running", category, metric.running));
            metrics.add(categoryMetric("ontrack.job.disabled", category, metric.disabled));
            metrics.add(categoryMetric("ontrack.job.error", category, metric.error));
            metrics.add(categoryMetric("ontrack.job.invalid", category, metric.invalid));
            metrics.add(categoryMetric("ontrack.job.paused", category, metric.paused));
        }

        // OK
        return metrics;
    }

    private TaggedMetric<?> categoryMetric(String name, String category, int value) {
        return TaggedMetric.of(name, value)
                .tag("category", category)
                .build();
    }

    @Data
    private static class JobMetric {
        int count = 0;
        int running = 0;
        int disabled = 0;
        int error = 0;
        int invalid = 0;
        int paused = 0;

        public static JobMetric getCategory(Map<String, JobMetric> categories, JobStatus status) {
            String category = status.getKey().getType().getCategory().getKey();
            return categories.computeIfAbsent(category, k -> new JobMetric());
        }
    }

}
