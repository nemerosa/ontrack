package net.nemerosa.ontrack.model.support

import org.apache.commons.lang3.StringUtils

data class MessageAnnotation(
        @JvmField val type: String?,
        val text: String?,
        val attributes: MutableMap<String, String> = mutableMapOf(),
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
