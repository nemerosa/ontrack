package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.extension.Extension

/**
 * Extension used to support a protocol to download a file.
 */
interface FileRefExtension : Extension {

    /**
     * Supported protocol
     */
    val protocol: String

    /**
     * Downloading a document using its path
     *
     * @param path Path extracted from the file ref URI
     * @param type Expected MIME type of the document
     * @return Document or null if not found
     */
    fun download(path: String, type: String): Document?

}