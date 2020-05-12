package net.nemerosa.ontrack.extension.indicators.values

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BooleanNode
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCompliance
import net.nemerosa.ontrack.extension.indicators.model.IndicatorValueType
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Selection
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.structure.Description
import net.nemerosa.ontrack.model.structure.NameDescription
import org.springframework.stereotype.Component

@Component
class BooleanIndicatorValueType(
        extension: IndicatorsExtensionFeature
) : AbstractExtension(extension), IndicatorValueType<Boolean, BooleanIndicatorValueTypeConfig> {

    override val name: String = "Yes/No"

    override fun status(config: BooleanIndicatorValueTypeConfig, value: Boolean): IndicatorCompliance =
            when {
                value -> IndicatorCompliance.HIGHEST
                config.required -> IndicatorCompliance.LOWEST
                else -> IndicatorCompliance.MEDIUM
            }

    override fun form(
            nameDescription: NameDescription,
            config: BooleanIndicatorValueTypeConfig,
            value: Boolean?
    ): Form = Form.create()
            .with(
                    Selection.of("value")
                            .label(nameDescription.name)
                            .help(nameDescription.description)
                            .items(
                                    listOf(
                                            Description("", "n/a", "Not applicable"),
                                            Description("true", "Yes", "Yes"),
                                            Description("false", "No", "No")
                                    )
                            )
                            .value(toClientValue(value))
            )

    override fun toClientJson(config: BooleanIndicatorValueTypeConfig, value: Boolean): JsonNode =
            mapOf(
                    "value" to toClientValue(value)
            ).asJson()

    override fun fromClientJson(config: BooleanIndicatorValueTypeConfig, value: JsonNode): Boolean? {
        val text = value.path("value").asText()
        return fromClientValue(text)
    }

    override fun fromStoredJson(valueConfig: BooleanIndicatorValueTypeConfig, value: JsonNode): Boolean? = when {
        value.isNull -> null
        value.isBoolean -> value.asBoolean()
        else -> null
    }

    override fun toStoredJson(config: BooleanIndicatorValueTypeConfig, value: Boolean): JsonNode {
        return BooleanNode.valueOf(value)
    }

    companion object {
        private const val CLIENT_NULL = ""
        private const val CLIENT_TRUE = "true"
        private const val CLIENT_FALSE = "false"

        private fun toClientValue(value: Boolean?) =
                when (value) {
                    null -> CLIENT_NULL
                    true -> CLIENT_TRUE
                    else -> CLIENT_FALSE
                }

        private fun fromClientValue(value: String) =
                when (value) {
                    CLIENT_TRUE -> true
                    CLIENT_FALSE -> false
                    else -> null
                }
    }

    override fun configForm(config: BooleanIndicatorValueTypeConfig?): Form =
            Form.create()
                    .with(
                            YesNo.of(BooleanIndicatorValueTypeConfig::required.name)
                                    .label("Required")
                                    .value(config?.required ?: true)
                    )

    override fun toConfigForm(config: BooleanIndicatorValueTypeConfig): JsonNode = config.asJson()

    override fun fromConfigForm(config: JsonNode): BooleanIndicatorValueTypeConfig {
        val required = JsonUtils.getBoolean(config, BooleanIndicatorValueTypeConfig::required.name, true)
        return BooleanIndicatorValueTypeConfig(required)
    }

    override fun toConfigClientJson(config: BooleanIndicatorValueTypeConfig): JsonNode =
            config.asJson()

    override fun toConfigStoredJson(config: BooleanIndicatorValueTypeConfig): JsonNode =
            config.asJson()

    override fun fromConfigStoredJson(config: JsonNode): BooleanIndicatorValueTypeConfig =
            config.parseOrNull<BooleanIndicatorValueTypeConfig>()
                    ?: BooleanIndicatorValueTypeConfig(required = true)
}

class BooleanIndicatorValueTypeConfig(
        val required: Boolean
)