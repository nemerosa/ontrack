package net.nemerosa.ontrack.common

/**
 * Representation of a "document", some binary content with a MIME type.
 *
 * @property type MIME type
 * @property content Binary content
 */
data class Document(val type: String, val content: ByteArray) {

    /**
     * Is the document not valid or empty?
     */
    val isEmpty: Boolean
        get() = "" == type || content.isEmpty()

    companion object {
        /**
         * Empty document
         */
        @JvmField
        val EMPTY = Document("", ByteArray(0))

        /**
         * Is the [document] not null and not empty
         */
        @JvmStatic
        fun isValid(document: Document?): Boolean {
            return document != null && !document.isEmpty
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Document) return false

        if (type != other.type) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }

}