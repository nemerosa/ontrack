package net.nemerosa.ontrack.model.support

import org.apache.commons.lang3.StringUtils

class MessageAnnotation(
        @JvmField val type: String?,
        private val text: String?,
        private val attributes: MutableMap<String, String> = mutableMapOf(),
) {
    fun attr(name: String, value: String): MessageAnnotation {
        attributes[name] = value
        return this
    }

    fun attr(name: String): String? {
        return attributes[name]
    }

    fun text(text: String?): MessageAnnotation {
        return MessageAnnotation(type, text, attributes)
    }

    fun isText(): Boolean {
        return type == null
    }

    fun hasText(): Boolean {
        return StringUtils.isNotBlank(text)
    }

    fun getText(): String? {
        return text
    }

    fun getAttributes(): Map<String, String> {
        return attributes
    }

    companion object {
        @JvmStatic
        fun of(type: String?): MessageAnnotation {
            return MessageAnnotation(type, null, HashMap())
        }

        @JvmStatic
        fun t(text: String?): MessageAnnotation {
            return of(null).text(text)
        }
    }
}
