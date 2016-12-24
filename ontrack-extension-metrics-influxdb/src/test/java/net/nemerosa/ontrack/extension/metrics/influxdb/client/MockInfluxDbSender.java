package net.nemerosa.ontrack.extension.metrics.influxdb.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MockInfluxDbSender implements InfluxDbSender {

    private final List<InfluxDbPoint> points = new ArrayList<>();

    public List<InfluxDbPoint> getPoints() {
        return points;
    }

    @Override
    public void flush() {
        points.clear();
    }

    @Override
    public boolean hasSeriesData() {
        return !points.isEmpty();
    }

    @Override
    public void appendPoints(InfluxDbPoint point) {
        points.add(point);
    }

    @Override
    public void writeData() throws Exception {
    }

    @Override
    public void setTags(Map<String, String> tags) {
    }
}
