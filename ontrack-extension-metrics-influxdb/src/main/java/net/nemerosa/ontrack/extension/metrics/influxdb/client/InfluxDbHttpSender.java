package net.nemerosa.ontrack.extension.metrics.influxdb.client;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of InfluxDbSender that writes to InfluxDb via the
 * https://github.com/influxdata/influxdb-java/ library.
 */
public class InfluxDbHttpSender implements InfluxDbSender {

    private final InfluxDB influxDB;
    private final BatchPoints batchPoints;
    private final TimeUnit timePrecision;

    /**
     * Creates a new http sender given connection details.
     *
     * @param hostname        the influxDb hostname
     * @param port            the influxDb http port
     * @param database        the influxDb database to write to
     * @param username        the influxDb username
     * @param password        the influxDb password
     * @param retentionPolicy Retention policy to use
     * @param timePrecision   the time precision of the metrics
     * @throws Exception exception while creating the influxDb sender(MalformedURLException)
     */
    public InfluxDbHttpSender(
            final String hostname,
            final int port,
            final String database,
            final String username,
            final String password,
            final String retentionPolicy,
            final TimeUnit timePrecision) throws Exception {
        this.timePrecision = timePrecision;
        this.influxDB = InfluxDBFactory.connect(
                String.format("http://%s:%d", hostname, port),
                username,
                password
        );
        // Creates the database
        this.influxDB.createDatabase(database);
        // Batch configuration
        batchPoints = BatchPoints
                .database(database)
                .tag("async", "true")
                .retentionPolicy(retentionPolicy)
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
    }

    @Override
    public synchronized void flush() {
        influxDB.write(batchPoints);
        batchPoints.getPoints().clear();
    }

    @Override
    public boolean hasSeriesData() {
        return batchPoints.getPoints() != null && !batchPoints.getPoints().isEmpty();
    }

    @Override
    public void appendPoints(InfluxDbPoint point) {
        if (point != null) {
            batchPoints.point(
                    Point.measurement(point.getMeasurement())
                            .fields(point.getFields())
                            .time(point.getTimestamp(), timePrecision)
                            .tag(point.getTags())
                            .build()
            );
        }
    }

    @Override
    public void setTags(Map<String, String> tags) {
        // FIXME Method net.nemerosa.ontrack.extension.metrics.influxdb.client.InfluxDbHttpSender.setTags

    }

    @Override
    public void writeData() throws Exception {
        flush();
    }

}
