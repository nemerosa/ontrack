package net.nemerosa.ontrack.extension.indicators.values

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCompliance
import net.nemerosa.ontrack.extension.indicators.model.IndicatorValueType
import net.nemerosa.ontrack.extension.indicators.support.Percentage
import net.nemerosa.ontrack.extension.indicators.support.PercentageThreshold
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.form.Form
import org.springframework.stereotype.Component

@Component
class IntegerIndicatorValueType(
        extension: IndicatorsExtensionFeature
) : AbstractExtension(extension), IndicatorValueType<Percentage, PercentageThreshold> {

    override val name: String = "Integer"

    override fun status(config: PercentageThreshold, value: Percentage): IndicatorCompliance =
            IndicatorCompliance(config.getCompliance(value).value)

    override fun form(config: PercentageThreshold, value: Percentage?): Form {
        TODO("Not yet implemented")
    }

    override fun toClientJson(config: PercentageThreshold, value: Percentage): JsonNode {
        TODO("Not yet implemented")
    }

    override fun fromClientJson(config: PercentageThreshold, value: JsonNode): Percentage? {
        TODO("Not yet implemented")
    }

    override fun fromStoredJson(valueConfig: PercentageThreshold, value: JsonNode): Percentage? {
        TODO("Not yet implemented")
    }

    override fun toStoredJson(config: PercentageThreshold, value: Percentage): JsonNode {
        TODO("Not yet implemented")
    }

    override fun configForm(config: PercentageThreshold?): Form {
        TODO("Not yet implemented")
    }

    override fun toConfigForm(config: PercentageThreshold): JsonNode {
        TODO("Not yet implemented")
    }

    override fun fromConfigForm(config: JsonNode): PercentageThreshold {
        TODO("Not yet implemented")
    }

    override fun toConfigClientJson(config: PercentageThreshold): JsonNode {
        TODO("Not yet implemented")
    }

    override fun toConfigStoredJson(config: PercentageThreshold): JsonNode {
        TODO("Not yet implemented")
    }

    override fun fromConfigStoredJson(config: JsonNode): PercentageThreshold {
        TODO("Not yet implemented")
    }
}
