package net.nemerosa.ontrack.extension.av.properties

import cc.ekblad.toml.decode
import cc.ekblad.toml.encodeToString
import cc.ekblad.toml.get
import cc.ekblad.toml.model.TomlDocument
import cc.ekblad.toml.model.TomlValue
import cc.ekblad.toml.tomlMapper
import org.springframework.stereotype.Component

/**
 * Support for TOML files.
 */
@Component
class TomlFilePropertyType : AbstractTextFilePropertyType() {

    override fun readProperty(content: String, targetProperty: String?): String? {
        val mapper = tomlMapper { }
        val toml = mapper.decode<TomlDocument>(content)
        val path = targetProperty ?: error("Target property is required")
        val tokens = path.split('.')
        return toml.get(*tokens.toTypedArray())
            ?.let {
                if (it is TomlValue.String) {
                    it.value
                } else {
                    null
                }
            }
    }

    override fun replaceProperty(content: String, targetProperty: String?, targetVersion: String): String {
        val mapper = tomlMapper {}
        val toml = mapper.decode<Map<String, Any>>(content).toMutableMap()

        val path = targetProperty ?: error("Target property is required")
        val tokens = path.split('.')

        update(toml, tokens, targetVersion)

        return mapper.encodeToString(toml)
    }

    private fun update(node: MutableMap<String, Any>, tokens: List<String>, value: String) {
        if (tokens.isNotEmpty()) {
            val name = tokens.first()
            val rest = tokens.drop(1)
            if (rest.isEmpty()) {
                node[name] = value
            } else {
                val child = node.getOrPut(name) {
                    mutableMapOf<String, Any>()
                }
                if (child is MutableMap<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    update(child as MutableMap<String, Any>, rest, value)
                }
            }
        }
    }

    override val id: String = "toml"

}