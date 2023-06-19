package net.nemerosa.ontrack.extension.indicators.ui

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorAttributeType
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorService
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorState
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Int
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * Getting UI information about the configurable indicators
 */
@RestController
@RequestMapping("/extension/indicators/configurables")
class ConfigurableIndicatorController(
    private val configurableIndicatorService: ConfigurableIndicatorService,
) : AbstractResourceController() {

    /**
     * Gets the edition form for a given configurable indicator
     *
     * @param id ID of the configurable indicator
     */
    @GetMapping("{id}/edit")
    fun getEditionForm(@PathVariable id: String): Form {
        val configurableIndicatorType = configurableIndicatorService.getConfigurableIndicatorType(id)
        val configurableIndicatorState =
            configurableIndicatorService.getConfigurableIndicatorState(configurableIndicatorType)
        // Common form fields
        val form = Form.create()
            .with(
                YesNo.of(ConfigurableIndicatorState::enabled.name)
                    .label("Enabled")
                    .help(getPropertyDescription(ConfigurableIndicatorState::enabled))
                    .value(configurableIndicatorState?.enabled)
            )
            .with(
                Text.of(ConfigurableIndicatorState::link.name)
                    .label("Link")
                    .help(getPropertyDescription(ConfigurableIndicatorState::link))
                    .optional()
                    .value(configurableIndicatorState?.link)
            )
        // Fields for the attributes
        configurableIndicatorType.attributes.forEach { attribute ->
            form.with(
                when (attribute.type) {
                    ConfigurableIndicatorAttributeType.INT -> Int.of(attribute.key)
                        .label(attribute.name)
                        .optional(!attribute.required)
                        .value(configurableIndicatorState?.getIntAttribute(attribute.key))
                    ConfigurableIndicatorAttributeType.REGEX -> Text.of(attribute.key)
                        .label(attribute.name)
                        .optional(!attribute.required)
                        .value(configurableIndicatorState?.getAttribute(attribute.key))
                    ConfigurableIndicatorAttributeType.REQUIRED -> YesNo.of(attribute.key)
                        .label(attribute.name)
                        .optional(!attribute.required)
                        .value(configurableIndicatorState?.getAttribute(attribute.key)?.toBoolean())
                }
            )
        }
        // OK
        return form
    }

    /**
     * Edits a configurable indicator
     *
     * @param id ID of the configurable indicator
     */
    @PutMapping("{id}/edit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun save(@PathVariable id: String, @RequestBody data: JsonNode) {
        // Gets the basic data
        val enabled = data.path(ConfigurableIndicatorState::enabled.name).asBoolean()
        val link = data.getTextField(ConfigurableIndicatorState::link.name)?.takeIf { it.isNotBlank() }
        // Extracts the attributes into a map
        val values = mutableMapOf<String, String?>()
        val configurableIndicatorType = configurableIndicatorService.getConfigurableIndicatorType(id)
        configurableIndicatorType.attributes.forEach { attribute ->
            val attributeValue = data.path(attribute.key).asText().takeIf { it.isNotBlank() }
            values[attribute.key] = attributeValue
        }
        // Saves the configurable indicator
        configurableIndicatorService.saveConfigurableIndicator(
            configurableIndicatorType,
            ConfigurableIndicatorState(
                enabled,
                link,
                ConfigurableIndicatorState.toAttributeList(configurableIndicatorType, values)
            )
        )
    }

}