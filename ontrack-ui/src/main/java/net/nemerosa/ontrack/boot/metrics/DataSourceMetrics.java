package net.nemerosa.ontrack.boot.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProviders;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class DataSourceMetrics implements PublicMetrics {

    private final Map<String, DataSource> dataSources;
    private final DataSourcePoolMetadataProviders provider;

    @Autowired
    public DataSourceMetrics(
            Map<String, DataSource> dataSources,
            Collection<DataSourcePoolMetadataProvider> providers) {
        this.dataSources = dataSources;
        this.provider = new DataSourcePoolMetadataProviders(providers);
    }

    @Override
    public Collection<Metric<?>> metrics() {
        List<Metric<?>> metrics = new ArrayList<>();
        dataSources.entrySet().stream().forEach(entry -> {
            String name = entry.getKey();
            String prefix = String.format("gauge.datasource.%s", name);
            DataSource dataSource = entry.getValue();
            DataSourcePoolMetadata poolMetadata = provider.getDataSourcePoolMetadata(dataSource);
            metrics.add(new Metric<>(prefix + ".active", poolMetadata.getActive()));
            metrics.add(new Metric<>(prefix + ".max", poolMetadata.getMax()));
            metrics.add(new Metric<>(prefix + ".min", poolMetadata.getMin()));
            metrics.add(new Metric<>(prefix + ".usage", poolMetadata.getUsage()));
        });
        return metrics;
    }

}
