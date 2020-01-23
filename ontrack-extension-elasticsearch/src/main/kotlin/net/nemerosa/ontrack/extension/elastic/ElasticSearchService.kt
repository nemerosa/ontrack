package net.nemerosa.ontrack.extension.elastic

import net.nemerosa.ontrack.model.structure.SearchIndexer
import net.nemerosa.ontrack.model.structure.SearchItem

/**
 * Service used to access the ElasticSearch instance.
 */
interface ElasticSearchService {

    fun <T : SearchItem> index(indexer: SearchIndexer<T>)

}