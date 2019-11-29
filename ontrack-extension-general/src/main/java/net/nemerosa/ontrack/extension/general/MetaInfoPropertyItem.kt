package net.nemerosa.ontrack.extension.general

import org.apache.commons.lang3.StringUtils
import java.util.regex.Pattern

class MetaInfoPropertyItem(
        val name: String,
        val value: String?,
        val link: String?,
        val category: String?
) {

    /**
     * Does one of the items match the name-&gt;value? The value can be blank (meaning all values)
     * or contains wildcards (*).
     */
    fun matchNameValue(namePattern: String, valuePattern: String): Boolean {
        return StringUtils.equals(this.name, namePattern) && (StringUtils.isBlank(valuePattern) ||
                StringUtils.equals("*", valuePattern) ||
                Pattern.matches(StringUtils.replace(valuePattern, "*", ".*"), this.value))
    }

    companion object {
        @JvmStatic
        fun of(name: String, value: String): MetaInfoPropertyItem {
            return MetaInfoPropertyItem(name, value, null, null)
        }
    }
}
