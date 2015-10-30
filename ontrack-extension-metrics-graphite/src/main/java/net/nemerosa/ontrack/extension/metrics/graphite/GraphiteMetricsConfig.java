package net.nemerosa.ontrack.extension.metrics.graphite;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
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
@ConditionalOnProperty(name = GraphiteMetricsConfigProperties.PREFIX, havingValue = "")
public class GraphiteMetricsConfig {

    private final Logger logger = LoggerFactory.getLogger(GraphiteMetricsConfig.class);

    @Autowired
    private GraphiteMetricsConfigProperties config;

    @Autowired
    private MetricRegistry registry;

    @Bean
    public Graphite graphite() {
        return new Graphite(
                config.getHost(),
                config.getPort()
        );
    }

    @Bean
    public GraphiteReporter graphiteReporter() {
        GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
                .prefixedWith("ontrack")
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite());
        logger.info(
                "[metrics] Starting Graphite reporting to {}:{}",
                config.getHost(),
                config.getPort());
        reporter.start(config.getPeriod(), TimeUnit.SECONDS);
        return reporter;
    }
}
