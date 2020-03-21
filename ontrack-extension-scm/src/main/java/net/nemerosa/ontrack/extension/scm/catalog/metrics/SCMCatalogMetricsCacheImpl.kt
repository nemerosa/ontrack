package net.nemerosa.ontrack.extension.scm.catalog.metrics

import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProjectFilterLink
import org.springframework.stereotype.Component

@Component
class SCMCatalogMetricsCacheImpl : SCMCatalogMetricsCache {

    override var counts: Map<SCMCatalogProjectFilterLink, Int> = emptyMap()

}