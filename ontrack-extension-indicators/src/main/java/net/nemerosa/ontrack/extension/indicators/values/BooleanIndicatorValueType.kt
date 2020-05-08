package net.nemerosa.ontrack.extension.indicators.values

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BooleanNode
import net.nemerosa.ontrack.extension.indicators.model.IndicatorStatus
import net.nemerosa.ontrack.extension.indicators.model.IndicatorValueType
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Selection
import net.nemerosa.ontrack.model.structure.Description
import net.nemerosa.ontrack.model.structure.NameDescription
import org.springframework.stereotype.Component

@Component
class BooleanIndicatorValueType : IndicatorValueType<Boolean, BooleanIndicatorValueTypeConfig> {

    override fun status(config: BooleanIndicatorValueTypeConfig, value: Boolean): IndicatorStatus =
            when {
                value -> IndicatorStatus.GREEN
                config.required -> IndicatorStatus.RED
                else -> IndicatorStatus.YELLOW
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
}

class BooleanIndicatorValueTypeConfig(
        val required: Boolean
)