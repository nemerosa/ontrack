package net.nemerosa.ontrack.extension.metrics.influxdb;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import net.nemerosa.ontrack.extension.metrics.influxdb.client.InfluxDbHttpSender;
import net.nemerosa.ontrack.extension.metrics.influxdb.client.InfluxDbReporter;
import net.nemerosa.ontrack.extension.metrics.influxdb.client.InfluxDbSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Enabling the metrics in Graphite.
 * <p>
 * The InfluxdbReporter from net.alchim31 is not compatible with InfluxDB 0.9, the official InfluxDBReporter
 * from DropWizards is not released yet
 * TODO Use DroWizard InfluxDB 4.x as soon as out
 */
@Configuration
@ConditionalOnProperty(name = InfluxdbMetricsConfigProperties.HOST_PROPERTY, havingValue = "")
public class InfluxdbMetricsConfig {

    private final Logger logger = LoggerFactory.getLogger(InfluxdbMetricsConfig.class);

    @Autowired
    private InfluxdbMetricsConfigProperties config;

    @Autowired
    private MetricRegistry registry;

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
