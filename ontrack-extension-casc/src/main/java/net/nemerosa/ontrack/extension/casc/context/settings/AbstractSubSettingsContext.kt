package net.nemerosa.ontrack.extension.casc.context.settings

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.context.AbstractCascContext
import net.nemerosa.ontrack.extension.casc.schema.CascType
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.parseInto
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import kotlin.reflect.KClass

abstract class AbstractSubSettingsContext<T : Any>(
    override val field: String,
    private val settingsClass: KClass<T>,
    private val settingsManagerService: SettingsManagerService,
) : AbstractCascContext(), SubSettingsContext {

    override val type: CascType
        get() = cascObject(settingsClass)

    override fun run(node: JsonNode, paths: List<String>) {
        // Parsing to the settings node
        val settings = try {
            parseSettings(node)
        } catch (ex: JsonParseException) {
            throw IllegalStateException(
                "Cannot parse settings $settingsClass at ${path(paths + field)}",
                ex
            )
        }
        // Sets the settings
        settingsManagerService.saveSettings(settings)
    }

    private fun parseSettings(node: JsonNode): T = node.parseInto(settingsClass)
}