package net.nemerosa.ontrack.extension.av.properties

abstract class AbstractTextFilePropertyType : FilePropertyType {

    override fun readProperty(content: List<String>, targetProperty: String?): String? {
        val text = getText(content)
        return readProperty(text, targetProperty)
    }

    abstract fun readProperty(content: String, targetProperty: String?): String?

    private fun getText(content: List<String>): String = content.joinToString("\n")

    override fun replaceProperty(content: List<String>, targetProperty: String?, targetVersion: String): List<String> {
        val text = getText(content)
        val output = replaceProperty(text, targetProperty, targetVersion)
        return output.lines()
    }

    abstract fun replaceProperty(content: String, targetProperty: String?, targetVersion: String): String

}