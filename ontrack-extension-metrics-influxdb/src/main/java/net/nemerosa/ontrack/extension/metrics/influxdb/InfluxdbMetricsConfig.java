package net.nemerosa.ontrack.extension.metrics.influxdb;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import metrics_influxdb.InfluxdbHttp;
import metrics_influxdb.InfluxdbReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Enabling the metrics in Graphite.
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
    public InfluxdbHttp influxdb() throws Exception {
        return new InfluxdbHttp(
                config.getHost(),
                config.getPort(),
                config.getDatabase(),
                config.getUser(),
                config.getPassword()
        );
    }

    @Bean
    public InfluxdbReporter influxdbReporter() throws Exception {
        InfluxdbReporter reporter = InfluxdbReporter
                .forRegistry(registry)
                .prefixedWith("ontrack")
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
