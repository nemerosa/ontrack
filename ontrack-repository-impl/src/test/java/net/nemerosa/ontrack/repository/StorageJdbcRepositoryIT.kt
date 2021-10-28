package net.nemerosa.ontrack.repository

import com.fasterxml.jackson.core.JsonProcessingException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Transactional
class StorageJdbcRepositoryIT : AbstractRepositoryTestSupport() {

    @Autowired
    private lateinit var repository: StorageRepository

    @Test
    fun no_data_returns_empty() {
        val o = repository.retrieveJson(uid("C"), "1")
        assertNull(o)
    }

    @Test
    fun store_and_retrieve() {
        val json = mapOf("name" to "My name").asJson()
        val store = uid("C")
        repository.storeJson(store, "1", json)
        val o = repository.retrieveJson(store, "1")
        assertNotNull(o) {
            assertEquals(json, it)
        }
    }

    @Test
    fun store_and_delete() {
        val json = mapOf("name" to "My name").asJson()
        val store = uid("C")
        repository.storeJson(store, "1", json)
        repository.delete(store, "1")
        val o = repository.retrieveJson(store, "1")
        assertNull(o)
    }

    @Test
    fun get_keys() {
        val store = uid("C")
        repository.storeJson(store, "1", mapOf("name" to "1").asJson())
        repository.storeJson(store, "2", mapOf("name" to "2").asJson())
        assertEquals(
            listOf("1", "2"),
            repository.getKeys(store)
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun get_data() {
        val store = uid("C")
        val data1 = mapOf("name" to "1").asJson()
        val data2 = mapOf("name" to "2").asJson()
        repository.storeJson(store, "1", data1)
        repository.storeJson(store, "2", data2)
        val data = repository.getData(store)
        assertEquals(2, data.size)
        assertEquals(data1, data["1"])
        assertEquals(data2, data["2"])
    }
}