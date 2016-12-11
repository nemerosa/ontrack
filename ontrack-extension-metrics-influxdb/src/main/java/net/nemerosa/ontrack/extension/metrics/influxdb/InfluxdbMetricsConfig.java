package net.nemerosa.ontrack.extension.metrics.influxdb;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import net.nemerosa.ontrack.extension.metrics.influxdb.client.InfluxDbHttpSender;
import net.nemerosa.ontrack.extension.metrics.influxdb.client.InfluxDbReporter;
import net.nemerosa.ontrack.extension.metrics.influxdb.client.InfluxDbSender;
import net.nemerosa.ontrack.model.metrics.OntrackTaggedMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Enabling the metrics in InfluxDB.
 */
@Configuration
@ConditionalOnProperty(name = InfluxdbMetricsConfigProperties.HOST_PROPERTY)
public class InfluxdbMetricsConfig {

    private final Logger logger = LoggerFactory.getLogger(InfluxdbMetricsConfig.class);

    private final InfluxdbMetricsConfigProperties config;

    private final MetricRegistry registry;

    private final Collection<OntrackTaggedMetrics> taggedMetrics;

    @Autowired
    public InfluxdbMetricsConfig(InfluxdbMetricsConfigProperties config, MetricRegistry registry, Collection<OntrackTaggedMetrics> taggedMetrics) {
        this.config = config;
        this.registry = registry;
        this.taggedMetrics = taggedMetrics;
    }

    @Bean
    public InfluxDbSender influxdb() throws Exception {
        return new InfluxDbHttpSender(
                config.getHost(),
                config.getPort(),
                config.getDatabase(),
                config.getUser(),
                config.getPassword(),
                config.getRetentionPolicy(),
                TimeUnit.MILLISECONDS
        );
    }

    @Bean
    public InfluxDbReporter influxdbReporter() throws Exception {
        InfluxDbReporter reporter = InfluxDbReporter
                .forRegistry(registry)
                .withTags(Collections.singletonMap("src", "ontrack"))
                .withTaggedMetrics(taggedMetrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .skipIdleMetrics(true) // Only report metrics that have changed.
                .build(influxdb());
        logger.info(
                "[metrics] Starting InfluxDB reporting to {}@{}:{}/{}",
                config.getUser(),
                config.getHost(),
                config.getPort(),
                config.getDatabase());
        reporter.start(config.getPeriod(), TimeUnit.SECONDS);
        return reporter;
    }

}
