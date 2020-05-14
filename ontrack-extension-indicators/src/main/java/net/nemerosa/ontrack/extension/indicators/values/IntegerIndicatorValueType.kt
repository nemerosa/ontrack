package net.nemerosa.ontrack.extension.indicators.values

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCompliance
import net.nemerosa.ontrack.extension.indicators.model.IndicatorValueType
import net.nemerosa.ontrack.extension.indicators.support.IntegerThresholds
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.form.Form
import org.springframework.stereotype.Component

@Component
class IntegerIndicatorValueType(
        extension: IndicatorsExtensionFeature
) : AbstractExtension(extension), IndicatorValueType<Int, IntegerThresholds> {

    override val name: String = "Number"

    override fun form(config: IntegerThresholds, value: Int?): Form {
        TODO("Not yet implemented")
    }

    override fun status(config: IntegerThresholds, value: Int): IndicatorCompliance =
            IndicatorCompliance(config.getCompliance(value).value)

    override fun toClientJson(config: IntegerThresholds, value: Int): JsonNode {
        TODO("Not yet implemented")
    }

    override fun fromClientJson(config: IntegerThresholds, value: JsonNode): Int? {
        TODO("Not yet implemented")
    }

    override fun fromStoredJson(valueConfig: IntegerThresholds, value: JsonNode): Int? {
        TODO("Not yet implemented")
    }

    override fun toStoredJson(config: IntegerThresholds, value: Int): JsonNode {
        TODO("Not yet implemented")
    }

    override fun configForm(config: IntegerThresholds?): Form {
        TODO("Not yet implemented")
    }

    override fun toConfigForm(config: IntegerThresholds): JsonNode {
        TODO("Not yet implemented")
    }

    override fun fromConfigForm(config: JsonNode): IntegerThresholds {
        TODO("Not yet implemented")
    }

    override fun toConfigClientJson(config: IntegerThresholds): JsonNode {
        TODO("Not yet implemented")
    }

    override fun toConfigStoredJson(config: IntegerThresholds): JsonNode {
        TODO("Not yet implemented")
    }

    override fun fromConfigStoredJson(config: JsonNode): IntegerThresholds {
        TODO("Not yet implemented")
    }
}
