package net.nemerosa.ontrack.extension.general.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.IntNode
import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataInputException
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.AbstractValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.springframework.stereotype.Component

data class ThresholdPercentageValidationDataTypeConfig(val threshold: Int?, val okIfGreater: Boolean = false)

@Component
class ThresholdPercentageValidationDataType(
        extensionFeature: GeneralExtensionFeature
) : AbstractValidationDataType<ThresholdPercentageValidationDataTypeConfig, Int>(
        extensionFeature
) {
    override fun configFromJson(node: JsonNode?): ThresholdPercentageValidationDataTypeConfig =
            JsonUtils.parse(node, ThresholdPercentageValidationDataTypeConfig::class.java)

    override fun configToJson(config: ThresholdPercentageValidationDataTypeConfig): JsonNode =
            JsonUtils.format(config)

    override fun getConfigForm(config: ThresholdPercentageValidationDataTypeConfig?): Form = Form.create()
            .with(net.nemerosa.ontrack.model.form.Int
                    .of("threshold")
                    .label("Threshold")
                    .value(config?.threshold)
                    .min(0).max(100)
                    .optional()
            )
            .with(net.nemerosa.ontrack.model.form.YesNo
                    .of("okIfGreater")
                    .label("Valid if greater?")
                    .value(config?.okIfGreater)
            )

    override fun fromConfigForm(node: JsonNode): ThresholdPercentageValidationDataTypeConfig =
            JsonUtils.parse(node, ThresholdPercentageValidationDataTypeConfig::class.java)

    override fun toJson(data: Int): JsonNode =
            IntNode(data)

    override fun fromJson(node: JsonNode): Int? {
        if (node is IntNode) {
            return node.asInt()
        } else {
            throw ValidationRunDataInputException(
                    "Data is expected to be an integer."
            )
        }
    }

    override fun getForm(data: Int?): Form = Form.create()
            .with(net.nemerosa.ontrack.model.form.Int
                    .of("value")
                    .label("Value (%)")
                    .value(data)
                    .min(0).max(100)
                    .optional()
            )

    override fun fromForm(node: JsonNode): Int? {
        if (node.has("value")) {
            return node.get("value").asInt()
        } else {
            return null
        }
    }

    override fun computeStatus(config: ThresholdPercentageValidationDataTypeConfig?, data: Int): ValidationRunStatusID? {
        if (config?.threshold != null) {
            return if (data >= config.threshold && config.okIfGreater) {
                ValidationRunStatusID.STATUS_PASSED
            } else {
                ValidationRunStatusID.STATUS_FAILED
            }
        } else {
            return null
        }
    }

    override fun validateData(config: ThresholdPercentageValidationDataTypeConfig?, data: Int) {
        validate(data >= 0, "Percentage must be >= 0")
    }

    override val displayName = "Percentage with threshold"
}