package net.nemerosa.ontrack.extension.general.validation

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.AbstractValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.springframework.stereotype.Component

data class FractionValidationDataTypeConfig(val threshold: Int?, val okIfGreater: Boolean = false)
data class FractionValidationData(val numerator: Int, val denominator: Int)

@Component
class FractionValidationDataType(
        extensionFeature: GeneralExtensionFeature
) : AbstractValidationDataType<FractionValidationDataTypeConfig, FractionValidationData>(
        extensionFeature
) {
    override fun configFromJson(node: JsonNode?): FractionValidationDataTypeConfig =
            JsonUtils.parse(node, FractionValidationDataTypeConfig::class.java)

    override fun configToJson(config: FractionValidationDataTypeConfig): JsonNode =
            JsonUtils.format(config)

    override fun getConfigForm(config: FractionValidationDataTypeConfig?): Form = Form.create()
            .with(net.nemerosa.ontrack.model.form.Int
                    .of("threshold")
                    .label("Threshold (%)")
                    .value(config?.threshold)
                    .min(0).max(100)
                    .optional()
            )
            .with(net.nemerosa.ontrack.model.form.YesNo
                    .of("okIfGreater")
                    .label("Valid if greater?")
                    .value(config?.okIfGreater)
            )

    override fun fromConfigForm(node: JsonNode): FractionValidationDataTypeConfig =
            JsonUtils.parse(node, FractionValidationDataTypeConfig::class.java)

    override fun toJson(data: FractionValidationData): JsonNode =
            JsonUtils.format(data)

    override fun fromJson(node: JsonNode): FractionValidationData? =
            JsonUtils.parse(node, FractionValidationData::class.java)

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

    override fun fromForm(node: JsonNode): FractionValidationData? =
            JsonUtils.parse(node, FractionValidationData::class.java)

    override fun computeStatus(config: FractionValidationDataTypeConfig?, data: FractionValidationData): ValidationRunStatusID? {
        if (config?.threshold != null) {
            return if ((data.numerator * 100 / data.denominator) >= config.threshold && config.okIfGreater) {
                ValidationRunStatusID.STATUS_PASSED
            } else {
                ValidationRunStatusID.STATUS_FAILED
            }
        } else {
            return null
        }
    }

    override fun validateData(config: FractionValidationDataTypeConfig?, data: FractionValidationData) {
        validate(data.numerator >= 0, "Numerator must be >= 0")
        validate(data.denominator > 0, "Denominator must be >= 0")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val displayName = "Fraction with threshold"
}