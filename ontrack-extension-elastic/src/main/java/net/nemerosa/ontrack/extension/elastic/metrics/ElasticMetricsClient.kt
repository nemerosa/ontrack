package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.model.structure.SearchNodeResults

interface ElasticMetricsClient {

    fun saveMetric(entry: ECSEntry)

    fun rawSearch(
        token: String,
        offset: Int = 0,
        size: Int = 10,
    ): SearchNodeResults

}