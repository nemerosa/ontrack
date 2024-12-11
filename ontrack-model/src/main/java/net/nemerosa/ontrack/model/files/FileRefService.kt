package net.nemerosa.ontrack.model.files

import net.nemerosa.ontrack.common.Document

interface FileRefService {

    /**
     * Given a file reference, returns a document.
     *
     * @param ref File reference
     * @param type [Document type][Document.type]
     * @return Document or null if not found
     */
    fun downloadDocument(ref: FileRef, type: String): Document?

}