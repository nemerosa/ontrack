package net.nemerosa.ontrack.extension.av.properties.yaml

import net.nemerosa.ontrack.extension.av.properties.AbstractTextFilePropertyType
import org.springframework.stereotype.Component

@Component
class YamlFilePropertyType : AbstractTextFilePropertyType() {

    override fun readProperty(content: String, targetProperty: String?): String? =
        YamlAccessor(content).getValue(
            targetProperty ?: error("targetProperty must be defined")
        )?.toString()

    override fun replaceProperty(content: String, targetProperty: String?, targetVersion: String): String {
        val property = targetProperty ?: error("targetProperty must be defined")
        return YamlAccessor(content).apply {
            setValue(property, targetVersion)
        }.write()
    }

    override val id: String = "yaml"
}