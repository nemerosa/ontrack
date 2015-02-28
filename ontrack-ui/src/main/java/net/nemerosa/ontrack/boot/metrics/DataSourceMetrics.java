package net.nemerosa.ontrack.boot.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProviders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;

@Component
public class DataSourceMetrics {

    private final Logger logger = LoggerFactory.getLogger(DataSourceMetrics.class);

    private final Map<String, DataSource> dataSources;
    private final GaugeService gaugeService;

    private final DataSourcePoolMetadataProviders provider;

    @Autowired
    public DataSourceMetrics(
            Map<String, DataSource> dataSources,
            Collection<DataSourcePoolMetadataProvider> providers,
            GaugeService gaugeService) {
        this.dataSources = dataSources;
        this.gaugeService = gaugeService;
        this.provider = new DataSourcePoolMetadataProviders(providers);
    }

    @Scheduled(fixedDelay = 5_000L)
    public void run() {
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
