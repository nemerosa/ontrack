package net.nemerosa.ontrack.boot.metrics;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.nemerosa.ontrack.model.metrics.MetricsSource;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.dropwizard.DropwizardMetricServices;
import org.springframework.boot.actuate.metrics.export.AbstractMetricExporter;
import org.springframework.boot.actuate.metrics.writer.DropwizardMetricWriter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// @Component
@Deprecated
// FIXME See how Ontrack metrics are exported to the outside
public class DefaultMetricsExporter extends AbstractMetricExporter {

    private final Logger logger = LoggerFactory.getLogger(DefaultMetricsExporter.class);

    private final OntrackConfigProperties config;
    private final DropwizardMetricServices metricWriter;
    private final ScheduledExecutorService executor;
    private final Collection<MetricsSource> metricsSources;

    @Autowired
    public DefaultMetricsExporter(OntrackConfigProperties config, DropwizardMetricServices metricWriter, Collection<MetricsSource> metricsSources) {
        super("");
        this.config = config;
        this.metricWriter = metricWriter;
        this.metricsSources = metricsSources;
        this.executor = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .setNameFormat("metrics-exporter-%d")
                        .build()
        );
    }

    @PostConstruct
    public void run() {
        logger.info("Scheduling export of metrics");
        executor.scheduleAtFixedRate(() -> {
            try {
                logger.trace("Exporting...");
                export();
            } catch (RuntimeException ex) {
                logger.error("RuntimeException thrown from {}#export. Exception was suppressed.", getClass().getSimpleName(), ex);
            }
        }, config.getMetricsPeriod(), config.getMetricsPeriod(), TimeUnit.SECONDS);
    }

    @Override
    protected Iterable<Metric<?>> next(String group) {
        return metricsSources.stream().flatMap(MetricsSource::getMetrics).collect(Collectors.toList());
    }

    @Override
    protected void write(String group, Collection<Metric<?>> values) {
        values.forEach(this::write);
    }

    private void write(Metric<?> metric) {
        logger.trace("{} -> {}", metric.getName(), metric.getValue());
        // TODO metricWriter.set(metric);
    }

}
