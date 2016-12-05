package net.nemerosa.ontrack.extension.metrics.influxdb.client;

import java.util.Map;

public interface InfluxDbSender {
    /**
     * Flushes buffer, if applicable.
     */
    void flush();

    /**
     * @return true if there is data available to send.
     */
    boolean hasSeriesData();

    /**
     * Adds this metric point to the buffer.
     *
     * @param point metric point with tags and fields
     */
    void appendPoints(final InfluxDbPoint point);

    /**
     * Writes buffer data to InfluxDb.
     *
     * @throws Exception exception while writing to InfluxDb api
     */
    void writeData() throws Exception;

    /**
     * Set tags applicable for all the points.
     *
     * @param tags map containing tags common to all metrics
     */
    void setTags(final Map<String, String> tags);
}
