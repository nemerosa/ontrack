package net.nemerosa.ontrack.extension.av.properties

interface FilePropertyType {

    val id: String

    fun readProperty(content: List<String>, targetProperty: String?): String?

    fun replaceProperty(content: List<String>, targetProperty: String?, targetVersion: String): List<String>

}