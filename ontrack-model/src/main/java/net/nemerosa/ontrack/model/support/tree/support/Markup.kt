package net.nemerosa.ontrack.model.support.tree.support

import com.fasterxml.jackson.annotation.JsonIgnore

data class Markup(
    val type: String?,
    val text: String?,
    val attributes: Map<String, String?>?,
) {

    fun attrs(attributes: Map<String, String?>) = copy(attributes = (this.attributes ?: emptyMap()) + attributes)

    fun attr(name: String, value: String?) = copy(
        attributes = (attributes ?: emptyMap()) + (name to value)
    )

    @get:JsonIgnore
    val isOnlyText: Boolean = type == null

    companion object {
        @JvmStatic
        fun text(text: String?): Markup {
            return Markup(null, text, null)
        }

        @JvmStatic
        fun of(type: String?): Markup {
            return Markup(type, null, emptyMap())
        }

        @JvmStatic
        fun of(type: String?, attributes: Map<String, String?>): Markup {
            return of(type).attrs(attributes)
        }
    }
}
