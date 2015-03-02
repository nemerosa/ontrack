package net.nemerosa.ontrack.boot.metrics;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProviders;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DataSourceMetrics {

    private final Logger logger = LoggerFactory.getLogger(DataSourceMetrics.class);

    private final OntrackConfigProperties config;
    private final Map<String, DataSource> dataSources;
    private final GaugeService gaugeService;

    private final ScheduledExecutorService executor;
    private final DataSourcePoolMetadataProviders provider;

    @Autowired
    public DataSourceMetrics(
            Map<String, DataSource> dataSources,
            Collection<DataSourcePoolMetadataProvider> providers,
            OntrackConfigProperties config, GaugeService gaugeService) {
        this.dataSources = dataSources;
        this.config = config;
        this.gaugeService = gaugeService;
        this.provider = new DataSourcePoolMetadataProviders(providers);
        this.executor = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .setNameFormat("datasource-metrics-exporter-%d")
                        .build()
        );
    }

    @PostConstruct
    public void run() {
        logger.info("Scheduling datasource metrics");
        executor.scheduleAtFixedRate(() -> {
            try {
                report();
            } catch (RuntimeException ex) {
                logger.error("RuntimeException thrown from {}#report. Exception was suppressed.", DataSourceMetrics.this.getClass().getSimpleName(), ex);
            }
        }, config.getGraphitePeriod(), config.getGraphitePeriod(), TimeUnit.SECONDS);
    }

    protected void report() {
        logger.trace("Sending datasource metrics");
        dataSources.entrySet().stream().forEach(entry -> {
            String name = entry.getKey();
            String prefix = String.format("datasource.%s", name);
            DataSource dataSource = entry.getValue();
            DataSourcePoolMetadata poolMetadata = provider.getDataSourcePoolMetadata(dataSource);
            gaugeService.submit(prefix + ".active", poolMetadata.getActive());
            gaugeService.submit(prefix + ".max", poolMetadata.getMax());
            gaugeService.submit(prefix + ".min", poolMetadata.getMin());
            gaugeService.submit(prefix + ".usage", poolMetadata.getUsage());
        });
    }
}
