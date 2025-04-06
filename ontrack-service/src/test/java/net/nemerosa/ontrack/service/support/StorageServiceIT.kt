package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.support.StorageService
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StorageServiceIT : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var storageService: StorageService

    @Test
    fun store_type_none() {
        val retrieved = storageService.find(
            TestUtils.uid("C"), "1",
            StoredValue::class
        )
        assertNotNull(retrieved)
    }

    @Test
    fun store_type() {
        val stored = StoredValue("test")
        val store = TestUtils.uid("C")
        storageService.store(store, "1", stored)
        val retrieved = storageService.find(
            store, "1",
            StoredValue::class
        )
        assertNotNull(retrieved)
        assertEquals(stored, retrieved)
    }

    @Test
    fun store_Data() {
        val stored1 = StoredValue("test-1")
        val stored2 = StoredValue("test-2")
        val store = TestUtils.uid("C")

        storageService.store(store, "1", stored1)
        storageService.store(store, "2", stored2)

        val data = storageService.getData(
            store,
            StoredValue::class.java
        )

        assertEquals(2, data.size.toLong())
        assertEquals(stored1, data["1"])
        assertEquals(stored2, data["2"])
    }
}
