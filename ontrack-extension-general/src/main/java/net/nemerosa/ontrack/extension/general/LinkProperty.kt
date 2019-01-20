package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.support.NameValue

data class LinkProperty(
        val links: List<NameValue>
) {

    companion object {
        @JvmStatic
        fun of(name: String, value: String): LinkProperty {
            return LinkProperty(
                    listOf(
                            NameValue(name, value)
                    )
            )
        }
    }
}
