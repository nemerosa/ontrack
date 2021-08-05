package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorAttributeType
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorService
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorState
import net.nemerosa.ontrack.graphql.support.getDescription
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Int
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
                    .help(getDescription(ConfigurableIndicatorState::enabled))
                    .value(configurableIndicatorState?.enabled)
            )
            .with(
                Text.of(ConfigurableIndicatorState::link.name)
                    .label("Link")
                    .help(getDescription(ConfigurableIndicatorState::link))
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
                }
            )
        }
        // OK
        return form
    }

}