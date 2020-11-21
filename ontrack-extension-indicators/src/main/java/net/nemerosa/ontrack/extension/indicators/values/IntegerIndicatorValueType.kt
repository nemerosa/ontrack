package net.nemerosa.ontrack.extension.indicators.values

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCompliance
import net.nemerosa.ontrack.extension.indicators.model.IndicatorValueType
import net.nemerosa.ontrack.extension.indicators.support.IntegerThresholds
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.YesNo
import org.springframework.stereotype.Component

typealias IntField = net.nemerosa.ontrack.model.form.Int

@Component
class IntegerIndicatorValueType(
        extension: IndicatorsExtensionFeature
) : AbstractExtension(extension), IndicatorValueType<Int, IntegerThresholds> {

    override val name: String = "Number"

    override fun form(config: IntegerThresholds, value: Int?): Form =
            Form.create()
                    .with(
                            IntField.of("value")
                                    .optional()
                                    .label("Value")
                                    .min(0)
                                    .value(value)
                    )

    override fun status(config: IntegerThresholds, value: Int): IndicatorCompliance =
            IndicatorCompliance(config.getCompliance(value).value)

    override fun toClientJson(config: IntegerThresholds, value: Int): JsonNode =
            mapOf("value" to value).asJson()

    override fun fromClientJson(config: IntegerThresholds, value: JsonNode): Int? {
        val i = JsonUtils.getInt(value, "value", -1)
        return if (i < 0) {
            null
        } else {
            i
        }
    }

    override fun fromStoredJson(valueConfig: IntegerThresholds, value: JsonNode): Int? =
            if (value.isInt) {
                value.asInt()
            } else {
                null
            }

    override fun toStoredJson(config: IntegerThresholds, value: Int): JsonNode =
            value.asJson()

    override fun configForm(config: IntegerThresholds?): Form =
            Form.create()
                    .with(
                            IntField.of(IntegerThresholds::min.name)
                                    .label("Min")
                                    .min(0)
                                    .value(config?.min ?: DEFAULT.min)
                    )
                    .with(
                            IntField.of(IntegerThresholds::max.name)
                                    .label("Max")
                                    .min(0)
                                    .value(config?.max ?: DEFAULT.max)
                    )
                    .with(
                            YesNo.of(IntegerThresholds::higherIsBetter.name)
                                    .label("Higher is better")
                                    .value(config?.higherIsBetter ?: DEFAULT.higherIsBetter)
                    )

    override fun toConfigForm(config: IntegerThresholds): JsonNode =
            config.asJson()

    override fun fromConfigForm(config: JsonNode): IntegerThresholds =
            config.parseOrNull() ?: DEFAULT

    override fun toConfigClientJson(config: IntegerThresholds): JsonNode =
            config.asJson()

    override fun toConfigStoredJson(config: IntegerThresholds): JsonNode =
            config.asJson()

    override fun fromConfigStoredJson(config: JsonNode): IntegerThresholds =
            config.parseOrNull() ?: DEFAULT

    companion object {
        private val DEFAULT = IntegerThresholds(
                min = 0,
                max = 10,
                higherIsBetter = false
        )
    }
}
