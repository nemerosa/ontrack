package net.nemerosa.ontrack.service.support

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.asOptional
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.json.parseInto
import net.nemerosa.ontrack.model.support.StorageService
import net.nemerosa.ontrack.repository.StorageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.reflect.KClass

@Service
@Transactional
class StorageServiceImpl(
    private val repository: StorageRepository,
) : StorageService {

    override fun storeJson(store: String, key: String, node: JsonNode?) {
        if (node != null) {
            repository.storeJson(store, key, node)
        } else {
            repository.delete(store, key)
        }
    }

    override fun retrieveJson(store: String, key: String): Optional<JsonNode> =
        findJson(store, key).asOptional()

    override fun findJson(store: String, key: String): JsonNode? = repository.retrieveJson(store, key)

    override fun getKeys(store: String): List<String> = repository.getKeys(store)

    override fun getData(store: String): Map<String, JsonNode> = repository.getData(store)

    override fun delete(store: String, key: String) {
        repository.delete(store, key)
    }

    override fun exists(store: String, key: String): Boolean = repository.exists(store, key)

    override fun <T> findByJson(store: String, query: String, variables: Map<String, *>, type: Class<T>): List<T> {
        return repository.filter(
            store = store,
            offset = 0,
            size = Int.MAX_VALUE,
            query = query,
            queryVariables = variables,
        ).map {
            JsonUtils.parse(it, type)
        }
    }

    override fun count(store: String, offset: Int, size: Int, query: String?, queryVariables: Map<String, *>?): Int =
        repository.count(store, offset, size, query, queryVariables)

    override fun <T : Any> filter(
        store: String,
        type: KClass<T>,
        offset: Int,
        size: Int,
        query: String?,
        queryVariables: Map<String, *>?,
        orderQuery: String?
    ): List<T> =
        repository.filter(store, offset, size, query, queryVariables, orderQuery).map {
            it.parseInto(type)
        }

}
