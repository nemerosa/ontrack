package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

/**
 * This service is used to manage the search indexes when they are available.
 */
interface SearchIndexService {

    /**
     * Are search indexes available?
     */
    val searchIndexesAvailable: Boolean

    fun <T : SearchItem> createSearchIndex(indexer: SearchIndexer<T>, item: T)

    fun <T : SearchItem> updateSearchIndex(indexer: SearchIndexer<T>, item: T)

    fun <T : SearchItem> deleteSearchIndex(indexer: SearchIndexer<T>, id: String)

}

/**
 * Utility extension accepting any kind of ID.
 */
fun <T : SearchItem> SearchIndexService.deleteSearchIndex(indexer: SearchIndexer<T>, id: Any) =
        deleteSearchIndex(indexer, id.toString())

/**
 * Default implementation does not manage search indexes at all
 */
@Service
@ConditionalOnProperty(
        name = [OntrackConfigProperties.SEARCH_PROPERTY],
        havingValue = "default",
        matchIfMissing = true
)
class NOPSearchIndexService : SearchIndexService {

    override val searchIndexesAvailable: Boolean = false

    override fun <T : SearchItem> createSearchIndex(indexer: SearchIndexer<T>, item: T) {}

    override fun <T : SearchItem> updateSearchIndex(indexer: SearchIndexer<T>, item: T) {}

    override fun <T : SearchItem> deleteSearchIndex(indexer: SearchIndexer<T>, id: String) {}

}