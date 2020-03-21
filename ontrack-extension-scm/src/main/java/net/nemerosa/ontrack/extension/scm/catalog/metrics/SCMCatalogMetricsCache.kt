package net.nemerosa.ontrack.extension.scm.catalog.metrics

import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProjectFilterLink

interface SCMCatalogMetricsCache {

    var counts: Map<SCMCatalogProjectFilterLink, Int>

}