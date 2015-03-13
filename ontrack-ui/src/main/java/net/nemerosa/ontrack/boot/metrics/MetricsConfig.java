package net.nemerosa.ontrack.boot.metrics;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(name = "ontrack.config.graphite-host", havingValue = "")
public class MetricsConfig {

    private final Logger logger = LoggerFactory.getLogger(MetricsConfig.class);

    @Autowired
    private OntrackConfigProperties config;

    @Autowired
    private MetricRegistry registry;

    @Bean
    public Graphite graphite() {
        return new Graphite(
                config.getGraphiteHost(),
                config.getGraphitePort()
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
                config.getGraphiteHost(),
                config.getGraphitePort());
        reporter.start(config.getMetricsPeriod(), TimeUnit.SECONDS);
        return reporter;
    }

}
