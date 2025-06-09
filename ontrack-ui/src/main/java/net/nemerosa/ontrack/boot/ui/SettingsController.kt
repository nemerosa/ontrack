package net.nemerosa.ontrack.boot.ui

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.ObjectMapperFactory
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.SettingsManager
import net.nemerosa.ontrack.model.settings.SettingsManagerNotFoundException
import net.nemerosa.ontrack.model.settings.SettingsValidationException
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resource
import org.apache.commons.lang3.StringUtils
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

/**
 * Global settings management.
 */
@RestController
@RequestMapping("/rest/settings")
class SettingsController(
        private val securityService: SecurityService,
        private val settingsManagers: Collection<SettingsManager<*>>
) : AbstractResourceController() {

    private val objectMapper = ObjectMapperFactory.create()

    /**
     * Gets settings
     */
    @GetMapping("/{type:.*}")
    fun <T> getSettings(@PathVariable type: String): Resource<T> {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        @Suppress("UNCHECKED_CAST") val settings: T? = settingsManagers
                .filter { candidate ->
                    StringUtils.equals(
                            type,
                            getSettingsManagerName(candidate)
                    )
                }
                .map { it.settings }
                .firstOrNull() as T?
        return if (settings != null) {
            Resource.of(
                    settings,
                    uri(on(javaClass).getSettings<Any>(type))
            )
        } else {
            throw SettingsManagerNotFoundException(type)
        }
    }


    /**
     * Security
     */
    @PutMapping("/{type:.*}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun <T> updateSettings(@PathVariable type: String, @RequestBody settingsNode: JsonNode?): Ack {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        // Gets the settings manager by type
        @Suppress("UNCHECKED_CAST")
        val settingsManager: SettingsManager<T> = (settingsManagers
                .firstOrNull { candidate ->
                    StringUtils.equals(
                            type,
                            getSettingsManagerName(candidate)
                    )
                }
                ?: throw  SettingsManagerNotFoundException(type))
                as SettingsManager<T>
        // Parsing
        val settings: T
        try {
            settings = objectMapper.treeToValue(settingsNode!!, settingsManager.settingsClass)
        } catch (e: JsonProcessingException) {
            throw SettingsValidationException(e)
        }

        // Saves the settings
        settingsManager.saveSettings(settings)
        // OK
        return Ack.OK
    }

    private fun getSettingsManagerName(settingsManager: SettingsManager<*>): String {
        return settingsManager.id
    }

}
