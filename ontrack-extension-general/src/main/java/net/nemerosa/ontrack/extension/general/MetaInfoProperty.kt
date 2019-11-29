package net.nemerosa.ontrack.extension.general

class MetaInfoProperty(
        val items: List<MetaInfoPropertyItem>
) {
    /**
     * Does one of the items match the name-&gt;value? The value can be blank (meaning all values)
     * or contains wildcards (*).
     */
    fun matchNameValue(name: String, value: String): Boolean = items.any { item ->
        item.matchNameValue(name, value)
    }

    /**
     * Gets the property value for a given property name
     */
    fun getValue(name: String): String? {
        return items
                .filter { it.name == name }
                .map { it.value }
                .firstOrNull()
    }
}
