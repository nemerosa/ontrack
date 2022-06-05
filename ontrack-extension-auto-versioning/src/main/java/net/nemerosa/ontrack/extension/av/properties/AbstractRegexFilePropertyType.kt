package net.nemerosa.ontrack.extension.av.properties

import net.nemerosa.ontrack.common.replaceGroup

abstract class AbstractRegexFilePropertyType : FilePropertyType {

    override fun readProperty(content: List<String>, targetProperty: String?): String? {
        val regex = getPropertyRegex(targetProperty).toRegex()
        return content.firstNotNullOfOrNull { line ->
            regex.matchEntire(line)?.groups?.get(1)?.value
        }
    }

    override fun replaceProperty(content: List<String>, targetProperty: String?, targetVersion: String): List<String> {
        val regex = getPropertyRegex(targetProperty).toRegex()
        return content.map { line -> regex.replaceGroup(line, 1, targetVersion) }
    }

    abstract fun getPropertyRegex(property: String?): String

}