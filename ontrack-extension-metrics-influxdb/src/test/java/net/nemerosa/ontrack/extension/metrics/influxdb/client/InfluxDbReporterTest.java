package net.nemerosa.ontrack.extension.metrics.influxdb.client;

import com.codahale.metrics.MetricRegistry;
import net.nemerosa.ontrack.model.metrics.OntrackTaggedMetrics;
import net.nemerosa.ontrack.model.metrics.TaggedMetric;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InfluxDbReporterTest {

    @Test
    public void tagged_metrics() {
        MockInfluxDbSender sender = new MockInfluxDbSender();
        MetricRegistry registry = new MetricRegistry();

        OntrackTaggedMetrics taggedMetrics = mock(OntrackTaggedMetrics.class);
        when(taggedMetrics.getTaggedMetrics()).thenReturn(
                Arrays.asList(
                        TaggedMetric.of("my.metric", 10)
                                .tag("category", "Category1")
                                .build(),
                        TaggedMetric.of("my.metric", 20)
                                .tag("category", "Category2")
                                .build()
                )
        );

        InfluxDbReporter reporter = InfluxDbReporter.forRegistry(registry)
                .withTaggedMetrics(Collections.singleton(taggedMetrics))
                .build(sender);

        reporter.report();

        List<InfluxDbPoint> points = sender.getPoints();

        assertEquals("my.metric", points.get(0).getMeasurement());
        assertEquals("my.metric", points.get(1).getMeasurement());

        assertEquals("Category1", points.get(0).getTags().get("category"));
        assertEquals("Category2", points.get(1).getTags().get("category"));

        assertEquals(10, points.get(0).getFields().get("value"));
        assertEquals(20, points.get(1).getFields().get("value"));
    }

}
