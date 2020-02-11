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

    fun <T : SearchItem> initIndex(indexer: SearchIndexer<T>)

    fun <T : SearchItem> index(indexer: SearchIndexer<T>)

    fun <T : SearchItem> resetIndex(indexer: SearchIndexer<T>, reindex: Boolean): Boolean

    fun <T : SearchItem> createSearchIndex(indexer: SearchIndexer<T>, item: T)

    fun <T : SearchItem> updateSearchIndex(indexer: SearchIndexer<T>, item: T)

    fun <T : SearchItem> deleteSearchIndex(indexer: SearchIndexer<T>, id: String)

    fun <T : SearchItem> batchSearchIndex(indexer: SearchIndexer<T>, items: Collection<T>, mode: BatchIndexMode): BatchIndexResults

}

/**
 * Batch indexing mode
 */
enum class BatchIndexMode {
    /**
     * If ID already exists, leaves as-is
     */
    KEEP,
    /**
     * If ID already exists, replaces it
     */
    UPDATE
}

/**
 * Results of batch indexing
 */
data class BatchIndexResults(
        val added: Int,
        val updated: Int,
        val kept: Int,
        val deleted: Int
) {
    companion object {
        val NONE = BatchIndexResults(added = 0, updated = 0, kept = 0, deleted = 0)
        val KEEP = BatchIndexResults(added = 0, updated = 0, kept = 1, deleted = 0)
        val UPDATE = BatchIndexResults(added = 0, updated = 1, kept = 0, deleted = 0)
        val ADD = BatchIndexResults(added = 1, updated = 0, kept = 0, deleted = 0)
    }

    operator fun plus(other: BatchIndexResults) =
            BatchIndexResults(
                    added = added + other.added,
                    updated = updated + other.updated,
                    kept = kept + other.kept,
                    deleted = deleted + other.deleted
            )
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
        name = [OntrackConfigProperties.SEARCH_ENGINE_PROPERTY],
        havingValue = "default",
        matchIfMissing = true
)
class NOPSearchIndexService : SearchIndexService {

    override val searchIndexesAvailable: Boolean = false

    override fun <T : SearchItem> initIndex(indexer: SearchIndexer<T>) {}

    override fun <T : SearchItem> index(indexer: SearchIndexer<T>) {}

    override fun <T : SearchItem> resetIndex(indexer: SearchIndexer<T>, reindex: Boolean) = false

    override fun <T : SearchItem> createSearchIndex(indexer: SearchIndexer<T>, item: T) {}

    override fun <T : SearchItem> updateSearchIndex(indexer: SearchIndexer<T>, item: T) {}

    override fun <T : SearchItem> deleteSearchIndex(indexer: SearchIndexer<T>, id: String) {}

    override fun <T : SearchItem> batchSearchIndex(indexer: SearchIndexer<T>, items: Collection<T>, mode: BatchIndexMode): BatchIndexResults =
            BatchIndexResults(added = 0, updated = 0, kept = 0, deleted = 0)

}