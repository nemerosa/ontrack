package net.nemerosa.ontrack.boot.metrics;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.export.MetricCopyExporter;
import org.springframework.boot.actuate.metrics.reader.MetricReader;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MetricsExporter extends MetricCopyExporter {

    private final Logger logger = LoggerFactory.getLogger(MetricsExporter.class);
    private final OntrackConfigProperties config;
    private final ScheduledExecutorService executor;

    @Autowired
    public MetricsExporter(MetricReader reader, MetricWriter writer, OntrackConfigProperties config) {
        super(reader, writer);
        this.config = config;
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
                export();
            } catch (RuntimeException ex) {
                logger.error("RuntimeException thrown from {}#export. Exception was suppressed.", getClass().getSimpleName(), ex);
            }
        }, config.getMetricsPeriod(), config.getMetricsPeriod(), TimeUnit.SECONDS);
    }

}
