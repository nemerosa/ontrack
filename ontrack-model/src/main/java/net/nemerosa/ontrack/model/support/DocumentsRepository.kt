package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.common.DocumentInfo
import net.nemerosa.ontrack.model.Ack

/**
 * This service is used to store, retrieve and manage documents.
 *
 * Implementations may vary.
 */
interface DocumentsRepository {

    /**
     * Stores or updates a document
     *
     * @param store ID of the store
     * @param name Name inside the store
     * @param document Document to store
     */
    fun storeDocument(store: String, name: String, document: Document)

    /**
     * Gets the list of store names
     */
    fun getDocumentStores(): List<String>

    /**
     * List of names inside a store
     * @param store ID of the store
     * @return List of names
     */
    fun getDocumentNames(store: String): List<String>

    /**
     * Total size of a store
     * @param store ID of the store
     * @return Total size
     */
    fun getSize(store: String): Long

    /**
     * Total count of documents
     * @param store ID of the store
     * @return Count of documents
     */
    fun getCount(store: String): Long

    /**
     * Retrieves a document
     *
     * @param store ID of the store
     * @param name Name inside the store
     * @return Stored document or `null` if not found
     */
    fun loadDocument(store: String, name: String): Document?

    /**
     * Does a document exist?
     *
     * @param store ID of the store
     * @param name Name inside the store
     * @return `true` if the document exists
     */
    fun documentExists(store: String, name: String): Boolean

    /**
     * Gets info about a document
     *
     * @param store ID of the store
     * @param name Name inside the store
     * @return Stored document info or `null` if not found
     */
    fun getDocumentInfo(store: String, name: String): DocumentInfo?

    /**
     * Deletes a document
     *
     * @param store ID of the store
     * @param name Name inside the store
     * @return [Ack.OK] if the document did exist, [Ack.NOK] if not.
     */
    fun deleteDocument(store: String, name: String): Ack

}