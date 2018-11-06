package net.nemerosa.ontrack.extension.general.validation

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.toJson
import net.nemerosa.ontrack.model.form.Form
import org.springframework.stereotype.Component

data class FractionValidationData(val numerator: Int, val denominator: Int)

@Component
class FractionValidationDataType(
        extensionFeature: GeneralExtensionFeature
) : AbstractThresholdConfigValidationDataType<FractionValidationData>(
        extensionFeature
) {

    override fun toJson(data: FractionValidationData): JsonNode =
            data.toJson()!!

    override fun fromJson(node: JsonNode): FractionValidationData? =
            node.parse()

    override fun getForm(data: FractionValidationData?): Form = Form.create()
            .with(net.nemerosa.ontrack.model.form.Int
                    .of("numerator")
                    .label("Numerator")
                    .value(data?.numerator)
                    .min(0)
            )
            .with(net.nemerosa.ontrack.model.form.Int
                    .of("denominator")
                    .label("Denominator")
                    .value(data?.denominator)
                    .min(1)
            )

    override fun fromForm(node: JsonNode?): FractionValidationData? =
            node?.parse()

    override fun toIntValue(data: FractionValidationData): Int {
        return (data.numerator * 100 / data.denominator)
    }

    override fun validateData(config: ThresholdConfig?, data: FractionValidationData?) =
            validateNotNull(data) {
                validate(numerator >= 0, "Numerator must be >= 0")
                validate(denominator > 0, "Denominator must be > 0")
                validate(numerator <= denominator, "Numerator must <= denominator")
            }

    override val displayName = "Fraction"
}