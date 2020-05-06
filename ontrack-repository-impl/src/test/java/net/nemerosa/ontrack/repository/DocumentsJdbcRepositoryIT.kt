package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

class DocumentsJdbcRepositoryIT : AbstractRepositoryTestSupport() {

    @Autowired
    private lateinit var repository: DocumentsRepository

    private val store = "test"
    private val key = "path/image"
    private val type = "image/png"

    private val bytes = DocumentsJdbcRepositoryIT::class.java
            .getResourceAsStream("/image.png")
            .readAllBytes()

    private val size = bytes.size.toLong()

    private val document = Document(type, bytes)

    @Test
    fun `Saving and retrieving a document`() {
        repository.storeDocument(store, key, document)
        val stored = repository.loadDocument(store, key)
        assertEquals(document, stored)
    }

    @Test
    fun `Saving a document twice just updates it`() {
        repository.storeDocument(store, key, document)
        repository.storeDocument(store, key, document)
        val stored = repository.loadDocument(store, key)
        assertEquals(document, stored)
    }

    @Test
    fun `Get document names`() {
        val names: List<String> = (1..3).map { uid("D") }
        names.forEach { repository.storeDocument(store, it, document) }
        val storedNames: List<String> = repository.getDocumentNames(store)
        assertTrue(storedNames.containsAll(names))
    }

    @Test
    fun `Deleting documents`() {
        val name = uid("N")
        repository.storeDocument(store, name, document)
        assertTrue(repository.documentExists(store, name), "Document is available")
        repository.deleteDocument(store, name)
        assertFalse(repository.documentExists(store, name), "Document has been deleted")
    }

    @Test
    fun `Document info`() {
        val name = uid("N")
        val info = repository.getDocumentInfo(store, name)
        assertNull(info, "Document not created yet")
        repository.storeDocument(store, name, document)
        assertNotNull(repository.getDocumentInfo(store, name), "Document info available") {
            assertEquals(type, it.type)
            assertEquals(size, it.size)
        }
    }

    @Test
    fun `Document counts and sizes`() {
        val store = uid("S")
        val names = (1..3).map { uid("N") }
        names.forEach { repository.storeDocument(store, it, document) }
        assertEquals(3, repository.getCount(store))
        assertEquals(3 * size, repository.getSize(store))
    }

    @Test
    fun `Document counts and sizes for unknown store`() {
        val store = uid("S")
        assertEquals(0, repository.getCount(store))
        assertEquals(0, repository.getSize(store))
    }

    @Test
    fun `Document stores`() {
        val store = uid("S")
        val names = (1..3).map { uid("N") }
        names.forEach { repository.storeDocument(store, it, document) }
        val stores = repository.getDocumentStores()
        assertEquals(1, stores.count { it == store }, "Store counted once")
    }

}