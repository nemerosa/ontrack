package net.nemerosa.ontrack.model.metrics;

import java.util.Collection;

/**
 * Marker for tagged metrics which must be exported.
 */
public interface OntrackTaggedMetrics {

    /**
     * Tagged metrics
     */
    Collection<TaggedMetric<?>> getTaggedMetrics();

}
