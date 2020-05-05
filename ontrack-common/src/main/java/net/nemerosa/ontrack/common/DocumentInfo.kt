package net.nemerosa.ontrack.common

/**
 * Information about a document.
 *
 * @property type MIME type about the document
 * @property size Size of the document
 */
class DocumentInfo(
        val type: String,
        val size: Long
)