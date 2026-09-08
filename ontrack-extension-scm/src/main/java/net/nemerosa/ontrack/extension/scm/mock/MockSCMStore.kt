package net.nemerosa.ontrack.extension.scm.mock

import net.nemerosa.ontrack.json.parseInto
import net.nemerosa.ontrack.model.support.StorageService
import org.slf4j.LoggerFactory

/**
 * Where the mock SCM keeps its repositories so that they outlive the process.
 *
 * The default is [NoMockSCMStore], which keeps nothing: integration and acceptance tests
 * create their mock data inside the test and would only pay for the writes. A long-lived
 * instance configured with the mock SCM — the demo, or a local dev stack whose backend is
 * restarted after every Kotlin change — sets `ontrack.config.extension.scm.mock.persistent`
 * and gets [StorageMockSCMStore] instead.
 */
interface MockSCMStore {

    /**
     * All the repositories the store holds, used once when the mock SCM starts.
     */
    fun loadAll(): List<MockRepositoryData>

    /**
     * Saves a repository, replacing whatever was stored for the same name.
     */
    fun save(data: MockRepositoryData)

    /**
     * Removes a repository from the store.
     */
    fun delete(name: String)
}

/**
 * Keeps nothing, which is the mock SCM's historical behaviour.
 */
object NoMockSCMStore : MockSCMStore {
    override fun loadAll(): List<MockRepositoryData> = emptyList()
    override fun save(data: MockRepositoryData) = Unit
    override fun delete(name: String) = Unit
}

/**
 * Keeps the repositories in the generic storage, one entry per repository, keyed by its name.
 *
 * Each repository is written whole after every change to it. That is wasteful in a way a real
 * store would not be, and deliberately so: the mock SCM holds a demo's worth of data, and a
 * whole-snapshot write is the only kind that cannot leave the persisted repository disagreeing
 * with the one in memory about where a branch's commits stop — which is what commit ids are
 * derived from.
 */
class StorageMockSCMStore(
    private val storageService: StorageService,
) : MockSCMStore {

    /**
     * Reads and parses the entries one by one, rather than through the typed
     * `getData(store, type)`, so that one unreadable entry costs one repository instead of all
     * of them. That is what lets this stay something no one depends on: a version of Yontrack
     * that changes the snapshot finds the old ones unparseable, logs them and starts empty,
     * which is the behaviour the mock SCM had before it persisted anything.
     */
    override fun loadAll(): List<MockRepositoryData> =
        storageService.getData(STORE).mapNotNull { (key, json) ->
            try {
                json.parseInto(MockRepositoryData::class)
            } catch (ex: Exception) {
                logger.warn("Ignoring unreadable mock SCM repository \"$key\": ${ex.message}")
                null
            }
        }

    override fun save(data: MockRepositoryData) {
        storageService.store(STORE, data.name, data)
    }

    override fun delete(name: String) {
        storageService.delete(STORE, name)
    }

    companion object {
        internal const val STORE = "mock-scm"
        private val logger = LoggerFactory.getLogger(StorageMockSCMStore::class.java)
    }
}
